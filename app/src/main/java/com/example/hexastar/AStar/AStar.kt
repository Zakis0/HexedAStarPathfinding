package com.example.hexastar.AStar

import android.util.Log
import com.example.hexastar.A_STAR_DEBUG
import com.example.hexastar.DRAW_AUXILIARY_HEXES
import com.example.hexastar.HEXAGON_ORIENTATION
import com.example.hexastar.HexInfo
import com.example.hexastar.Hexagons.Hexagon
import com.example.hexastar.Hexagons.HexagonField
import com.example.hexastar.Hexagons.HexagonFieldView
import com.example.hexastar.NEIGHBOR_HEXAGON_BORDER_COLOR
import com.example.hexastar.NEIGHBOR_HEXAGON_COLOR
import com.example.hexastar.PROCESSED_HEXAGON_BORDER_COLOR
import com.example.hexastar.PROCESSED_HEXAGON_COLOR

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
            heap.add(startNode)
            while (path.isEmpty() && toSearch.isNotEmpty()) {
                doStepOfFindingPath(
                    nodeField,
                    startNode,
                    targetNode,
                    toSearch,
                    processed,
                    path,
                    heap
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
            val currentNode = heap.getAndPop() ?: return false
//            heap.printArray()
            Log.e(A_STAR_DEBUG, "CurrentNodeIndexes: ${currentNode.indexesInField}\n" +
                    "${currentNode.hexInfo.toStringWithConnection()}\n")
            Log.d(A_STAR_DEBUG, "-------------------------------")
            if (DRAW_AUXILIARY_HEXES &&
                currentNode != startNode &&
                currentNode != targetNode) {
                currentNode.hexagonColor = PROCESSED_HEXAGON_COLOR
                currentNode.borderColor = PROCESSED_HEXAGON_BORDER_COLOR
            }
            processed.add(currentNode)
            toSearch.remove(currentNode)
            if (currentNode == targetNode) {
                var currentPathNode = targetNode
                while (currentPathNode != startNode) {
                    path.add(currentPathNode)
                    Log.d(A_STAR_DEBUG, "Path indexes: ${currentPathNode.indexesInField}")
                    currentPathNode = currentPathNode.hexInfo.connection!!
                }
                return true
            }
            for (neighbor in nodeField.getNeighborsList(currentNode.indexesInField, HEXAGON_ORIENTATION)) {
                if (!neighbor.hexInfo.isPassable || processed.contains(neighbor)) {
                    continue
                }
                Log.w(A_STAR_DEBUG, "Neighbor $neighbor\n${neighbor.hexInfo}")
                if (DRAW_AUXILIARY_HEXES &&
                    neighbor != startNode &&
                    neighbor != targetNode &&
                    !processed.contains(neighbor))
                {
                    neighbor.hexagonColor = NEIGHBOR_HEXAGON_COLOR
                    neighbor.borderColor = NEIGHBOR_HEXAGON_BORDER_COLOR
                }
                val inSearch = toSearch.contains(neighbor)
                val costToNeighbor = currentNode.hexInfo.G + HexInfo.getDistance(currentNode, neighbor)
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