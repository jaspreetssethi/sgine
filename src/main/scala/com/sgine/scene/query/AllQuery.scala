package com.sgine.scene.query

import com.sgine.scene.{NodeQuery, Node}

/**
 * A query that retrieves all nodes.
 */
object AllQuery extends NodeQuery {
  def matches(node: Node) = true
}
