package com.sgine.scene


import query._
import view.NodeView

/**
 * Something that contains Nodes and can fetch the ones mathcing a query.
 */
trait NodeContainer extends Iterable[Node] {
  
  /**
   * Return an iterator over all the Node:s in the container.
   */
  def iterator = getNodes().iterator

  /**
   * Return all the Node:s in the container.
   */
  def getNodes(): Iterable[Node] = getNodes(AllQuery)

  /**
   * Return all the Node:s matching the specified condition in the container.
   */
  def getNodes(condition : Node => Boolean): Iterable[Node] = getNodes( ConditionNodeQuery( condition ) )

  /**
   * Return all the Node:s matching the specified query in the container.
   */
  def getNodes(query : NodeQuery): Iterable[Node]

  /**
   * Creates a view that shows the nodes in this container that match the query at any given moment.
   */
  def createView(query: NodeQuery): NodeView
}
