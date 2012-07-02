package org.sgine.datastore.query

import org.sgine.datastore._

/**
 * @author Matt Hicks <mhicks@sgine.org>
 */
case class DatastoreQuery[T <: Persistable](collection: DatastoreCollection[T],
                                            _skip: Int = 0,
                                            _limit: Int = Int.MaxValue,
                                            _filters: List[Filter[T, _]] = List.empty[Filter[T, _]],
                                            _sort: List[Sort[T, _]] = List.empty[Sort[T, _]])
                                           (implicit manifest: Manifest[T]) extends Iterable[T] {
  def skip(s: Int) = copy(_skip = s)

  def limit(l: Int) = copy(_limit = l)

  def filter(filter: Filter[T, _]) = copy(_filters = filter :: _filters)

  def sort(sort: Sort[T, _]) = copy(_sort = sort :: _sort)

  def iterator = collection.executeQuery(this)

  override def toString() = "DatastoreQuery(%s, skip = %s, limit = %s, filters = %s)".format(collection.name, _skip, _limit, _filters)
}