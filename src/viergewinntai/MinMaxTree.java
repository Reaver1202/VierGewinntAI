package viergewinntai;

import java.util.LinkedList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rusinda
 */
public class MinMaxTree {

    private Node data = new Node();
    private int playColumn;

    public int getMove() {
        return playColumn;
    }

    public MinMaxTree(int[][] field, int depth, int player) {

        recTree(field, depth, 0, data, player);

        recMinMax(data, player, 1);

        // nun prüfen in welchem Kindknoten von Data data.value steht --> Spalte, die man spielen soll
//         for Schleife
        int moveDepth = depth + 1;
        for (int i = 0; i < data.children.size(); i++) {
            if (!data.children.get(i).empty && data.children.get(i).value == data.value) {
                if(data.children.get(i).nodeDepth < moveDepth){
                    moveDepth = data.children.get(i).nodeDepth;
                    playColumn = i;
                }
            }
        }

    }

    private void recTree(int[][] processingField_prievious, int depth, int depth_current, Node currentNode, int player) {

        int[][] processingField_current;

        // End-Kondition für Rekursion. Dann wird aktuelle Node mit ihrem Tiefenwert und Score bestückt
        if (depth == depth_current) {
            processingField_current = VierGewinntAi.cloneArray((processingField_prievious));

            //Hier evaluate aufrufen, um werte zu bekommen
            currentNode.nodeDepth = depth_current;
            currentNode.value = ArtificialIntelligence.evaluate(processingField_current, player);
        } // next depth-step
        else {

            // go through columns from left to right
            for (int column = 0; column < 7; column++) {
                // processingField zurückseten von vorigem Rekursionsschritt
                processingField_current = VierGewinntAi.cloneArray((processingField_prievious));

                // go through rows of the current column until a free space to set a new coin
                // in the end "row" represents the coin that was set
                int row = 0;
                while (row < 6 && processingField_current[column][row] != 0) {
                    row++;
                }

                //detect if the column is full and skip  
                if (row > 5) {
                    currentNode.addChild(new Node());
                    currentNode.children.getLast().empty = true;
                    continue;
                }

                // depending on current depth player 1 or player 2 is chosen
                // depth is odd (1,3,5,...) = current player
                // depth is even (2,4,6,...) = opponent


                int nextPiece;
                if (player == 2) {
                    nextPiece = (depth_current + 1) % 2 + 1;
                } else {
                    nextPiece = 2 - (depth_current + 1) % 2;
                }
//            processingField_current[column][row] = depth_current % 2 +1;
                processingField_current[column][row] = nextPiece;
//                processingField_current[column][row] = player;

                // save current depth value to orientate in which tree depth it is
                currentNode.nodeDepth = depth_current;

                if (VierGewinntAi.mainGameEngine.checkWin(processingField_current)) {
                    currentNode.children.add(new Node());
                    currentNode.children.getLast().value = ArtificialIntelligence.evaluate(processingField_current, player);
                    currentNode.children.getLast().nodeDepth = depth_current +1;
                } else {

                    // new children for currentNode to operate the next recursion step
                    currentNode.children.add(new Node());

                    recTree(processingField_current, depth, depth_current + 1, currentNode.children.getLast(), player);
                }
            }
        }

    }

    private void recMinMax(Node currentNode, int player, int depth) {
        // depth_lpnode = depth of (l)ast (p)arent node

        if (currentNode.empty) {
            return;
        }

        // until last node with children (max_depth -1 means depth_lpnode = last parent node)
//        if (currentNode.nodeDepth != depth_lastParentNode) {
//            for (int i = 0; i < currentNode.children.size(); i++) {
//                recMinMax(currentNode.children.get(i), depth_lastParentNode, player);
//            }
//        }

        if (!currentNode.children.isEmpty()) {
            for (int i = 0; i < currentNode.children.size(); i++) {
                Node n;
                if (!(n = currentNode.children.get(i)).children.isEmpty()) {
                    recMinMax(n, player, depth + 1);
                }
            }

            // now we are in right depth. currentNode has 7 children

            int minOrMax = depth % 2;
            try {
                if (minOrMax == 1) {
                    // odd depth value --> maximize
//                    currentNode.value = max(currentNode.children);
                    max(currentNode);
                } else {
                    // even depth value --> minimize
//                    currentNode.value = min(currentNode.children);
                    min(currentNode);
                }
            } catch (AllNodesEmptyException ex) {
                currentNode.empty = true;
            }
        }
    }
    
    private void max(Node node) throws AllNodesEmptyException {
        int returnVal = 0;
        int childIndex = 0;
        boolean allNodesEmpty = true;
        for (int i = 0; i < node.children.size(); i++) {
            if (!node.children.get(i).empty) {
                returnVal = node.children.get(i).value;
                childIndex = i;
                allNodesEmpty = false;
                break;
            }
        }
        if (allNodesEmpty) {
            throw new AllNodesEmptyException();
        }
        // nodes muss die 7 (Kind)Knoten enthalten, dessen Max/Min Wert gefunden werden muss
        for (int i = 0; i < node.children.size(); i++) {
            if (!node.children.get(i).empty && node.children.get(i).value > returnVal) {
                returnVal = node.children.get(i).value;
                childIndex = i;
            }
        }

        node.value = returnVal;
        node.nodeDepth = node.children.get(childIndex).nodeDepth;
    }

    private int max(LinkedList<MinMaxTree.Node> nodes) throws AllNodesEmptyException {
        int returnVal = 0;
        boolean allNodesEmpty = true;
        for (int i = 0; i < nodes.size(); i++) {
            if (!nodes.get(i).empty) {
                returnVal = nodes.get(i).value;
                allNodesEmpty = false;
                break;
            }
        }
        if (allNodesEmpty) {
            throw new AllNodesEmptyException();
        }
        // nodes muss die 7 (Kind)Knoten enthalten, dessen Max/Min Wert gefunden werden muss
        for (int i = 0; i < nodes.size(); i++) {
            if (!nodes.get(i).empty && nodes.get(i).value > returnVal) {
                returnVal = nodes.get(i).value;
            }
        }

        if (returnVal == Integer.MAX_VALUE) {
            int test = 0;
        }
        return returnVal;
    }
    
    private void min(Node node) throws AllNodesEmptyException {
        int returnVal = 0;
        int childIndex = 0;
        boolean allNodesEmpty = true;
        for (int i = 0; i < node.children.size(); i++) {
            if (!node.children.get(i).empty) {
                returnVal = node.children.get(i).value;
                childIndex = i;
                allNodesEmpty = false;
                break;
            }
        }
        if (allNodesEmpty) {
            throw new AllNodesEmptyException();
        }

        for (int i = 0; i < node.children.size(); i++) {
            if (!node.children.get(i).empty && node.children.get(i).value < returnVal) {
                returnVal = node.children.get(i).value;
                childIndex = i;
            }
        }

        node.value = returnVal;
        node.nodeDepth = node.children.get(childIndex).nodeDepth;
    }

    private int min(LinkedList<MinMaxTree.Node> nodes) throws AllNodesEmptyException {
        int returnVal = 0;
        boolean allNodesEmpty = true;
        for (int i = 0; i < nodes.size(); i++) {
            if (!nodes.get(i).empty) {
                returnVal = nodes.get(i).value;
                allNodesEmpty = false;
                break;
            }
        }
        if (allNodesEmpty) {
            throw new AllNodesEmptyException();
        }

        for (int i = 0; i < nodes.size(); i++) {
            if (!nodes.get(i).empty && nodes.get(i).value < returnVal) {
                returnVal = nodes.get(i).value;
            }
        }

        return returnVal;
    }

    private class Node {

        public int value;
        public int nodeDepth;
        public boolean empty = false;
        public LinkedList<Node> children = new LinkedList<>();

        public void addChild(Node n) {
            children.add(n);
        }
    }

    private class AllNodesEmptyException extends Exception {
    }
}
