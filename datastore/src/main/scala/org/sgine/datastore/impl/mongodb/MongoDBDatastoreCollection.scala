package org.sgine.datastore.impl.mongodb

import org.sgine.datastore._
import org.sgine.datastore.converter.DataObjectConverter
import com.mongodb.BasicDBObject

import query.{SortDirection, Operator, DatastoreQuery}
import scala.collection.JavaConversions._
import java.util
import scala.Some

/**
 * @author Matt Hicks <mhicks@sgine.org>
 */
class MongoDBDatastoreCollection[T <: Persistable](val session: MongoDBDatastoreSession, val name: String)
                                                  (implicit val manifest: Manifest[T]) extends DatastoreCollection[T] {
  lazy val collection = session.database.getCollection(name)

  protected def persistInternal(ref: T) = {
    val dbo = DataObjectConverter.toDBObject(ref, this)
    ref.persistenceState match {
      case PersistenceState.NotPersisted => collection.insert(dbo)
      case PersistenceState.Persisted => {
        collection.findAndModify(new BasicDBObject("_id", ref.id), dbo)
      }
    }
  }

  protected def deleteInternal(ref: T) = {
    collection.findAndRemove(new BasicDBObject("_id", ref.id))
  }

  def byId(id: util.UUID) = DataObjectConverter.fromDBValue(collection.findOne(new BasicDBObject("_id", id)), this) match {
    case null => None
    case value => Some(value.asInstanceOf[T])
  }

  def executeQuery(query: DatastoreQuery[T]) = {
    val dbo = new BasicDBObject()
    val filters = query._filters.reverse
    filters.foreach {
      case filter => {
        val value = filter.operator match {
          case Operator.< => new BasicDBObject("$lt", DataObjectConverter.toDBValue(filter.value, this))
          case Operator.<= => new BasicDBObject("$lte", DataObjectConverter.toDBValue(filter.value, this))
          case Operator.> => new BasicDBObject("$gt", DataObjectConverter.toDBValue(filter.value, this))
          case Operator.>= => new BasicDBObject("$gte", DataObjectConverter.toDBValue(filter.value, this))
          case Operator.equal => DataObjectConverter.toDBValue(filter.value, this)
          case Operator.nequal => new BasicDBObject("$ne", DataObjectConverter.toDBValue(filter.value, this))
        }
        dbo.put(filter.field.name, value)
      }
    }
    var cursor = collection.find(dbo)
    if (query._skip > 0) {
      cursor = cursor.skip(query._skip)
    }
    if (query._limit > 0) {
      cursor = cursor.limit(query._limit)
    }
    val sort = query._sort.reverse
    if (sort.nonEmpty) {
      val sdbo = new BasicDBObject()
      sort.foreach {
        case s => {
          val direction = s.direction match {
            case SortDirection.Ascending => 1
            case SortDirection.Descending => -1
          }
          sdbo.put(s.field.name, direction)
        }
      }
      cursor = cursor.sort(sdbo)
    }
    asScalaIterator(cursor).map(entry => DataObjectConverter.fromDBValue(entry, this)).asInstanceOf[Iterator[T]]
  }

  def iterator = asScalaIterator(collection.find()).map(entry => DataObjectConverter.fromDBValue(entry, this)).asInstanceOf[Iterator[T]]
}