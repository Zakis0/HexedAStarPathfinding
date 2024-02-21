package com.example.hexastar.AStar

import android.util.Log
import com.example.hexastar.A_STAR_DEBUG
import com.example.hexastar.DEBUG
import com.example.hexastar.HexInfo
import com.example.hexastar.Hexagons.Hexagon
import com.example.hexastar.Hexagons.HexagonField
import com.example.hexastar.Hexagons.HexagonFieldView
import com.example.hexastar.NEIGHBOR_HEXAGON_BORDER_COLOR
import com.example.hexastar.NEIGHBOR_HEXAGON_COLOR
import com.example.hexastar.NodeTypes
import com.example.hexastar.PROCESSED_HEXAGON_BORDER_COLOR
import com.example.hexastar.PROCESSED_HEXAGON_COLOR
import com.example.hexastar.Params

class AStar {
    companion object {
        fun findPath(
            hexagonFieldView: HexagonFieldView,
            nodeField: HexagonField,
            startNode: Hexagon,
            targetNode: Hexagon,
            toSearch: MutableList<Hexagon>,
            processed: MutableList<Hexagon>,
            path: MutableList<Hexagon>,
            heap: Heap<Hexagon>,
        ): MutableList<Hexagon> {
            while (path.isEmpty() && toSearch.isNotEmpty()) {
                doStepOfFindingPath(
                    nodeField,
                    startNode,
                    targetNode,
                    toSearch,
                    processed,
                    path,
                    heap,
                )
                hexagonFieldView.invalidate()
            }
            return path
        }
        fun doStepOfFindingPath(
            nodeField: HexagonField,
            startNode: Hexagon,
            targetNode: Hexagon,
            toSearch: MutableList<Hexagon>,
            processed: MutableList<Hexagon>,
            path: MutableList<Hexagon>,
            heap: Heap<Hexagon>,
            ): Boolean {
            if (heap.isEmpty()) {
                return true
            }
            val currentNode = heap.getAndPop()!!
            Log.d(A_STAR_DEBUG, "Current heap elements")
            heap.printArray()
            Log.e(A_STAR_DEBUG, "CurrentNodeIndexes: ${currentNode.indexesInField}\n" +
                    "${currentNode.hexInfo.toStringWithConnection()}\n")
            Log.d(A_STAR_DEBUG, "-------------------------------")
            if (Params.DRAW_AUXILIARY_HEXES &&
                currentNode != startNode &&
                currentNode != targetNode
                ) {
                currentNode.hexInfo.type = NodeTypes.PROCESSED
                currentNode.hexagonColor = PROCESSED_HEXAGON_COLOR
                currentNode.borderColor = PROCESSED_HEXAGON_BORDER_COLOR
            }
            processed.add(currentNode)
            toSearch.remove(currentNode)
            if (currentNode == targetNode) {
                var currentPathNode = targetNode
                while (currentPathNode != startNode) {
                    path.add(currentPathNode)
                    currentPathNode.hexInfo.type = NodeTypes.PATH
                    Log.d(A_STAR_DEBUG, "Path indexes: ${currentPathNode.indexesInField}")
                    currentPathNode = currentPathNode.hexInfo.connection!!
                }
                return true
            }
            for (neighbor in nodeField.getNeighborsList(currentNode.indexesInField, Params.HEXAGON_ORIENTATION)) {
                if (!neighbor.hexInfo.isPassable || processed.contains(neighbor)) {
                    continue
                }
                Log.w(A_STAR_DEBUG, "Neighbor $neighbor ${neighbor.hexInfo}")
                if (Params.DRAW_AUXILIARY_HEXES &&
                    neighbor != startNode &&
                    neighbor != targetNode &&
                    !processed.contains(neighbor))
                {
                    neighbor.hexInfo.type = NodeTypes.NEIGHBOR
                    neighbor.hexagonColor = NEIGHBOR_HEXAGON_COLOR
                    neighbor.borderColor = NEIGHBOR_HEXAGON_BORDER_COLOR
                }
                val inSearch = toSearch.contains(neighbor)
                val costToNeighbor = currentNode.hexInfo.G + HexInfo.getDistance(currentNode, neighbor)

                Log.d(A_STAR_DEBUG, "currentNode.G ${currentNode.hexInfo.G}\n" +
                        "Distance ${HexInfo.getDistance(currentNode, neighbor)}\n" +
                        "costToNeighbor $costToNeighbor\n" +
                        "neighbor.G ${neighbor.hexInfo.G}")
                if (!inSearch || costToNeighbor < neighbor.hexInfo.G) {
                    neighbor.hexInfo.set_G(costToNeighbor)
                    neighbor.hexInfo.set_Connection(currentNode)
                    if (!inSearch) {
                        neighbor.hexInfo.set_H(HexInfo.getDistance(neighbor, targetNode))
                        toSearch.add(neighbor)
                        heap.add(neighbor)
                    }
                }
            }
            Log.d(A_STAR_DEBUG, "++++++++++++++++++++++++++++++++++")
            return false
        }
    }
}