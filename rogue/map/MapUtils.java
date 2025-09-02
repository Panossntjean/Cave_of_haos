package rogue.map;

import java.util.Queue;
import java.util.LinkedList;

import rogue.map.GameMap;
import rogue.map.TileType;

public class MapUtils {


    public static int[] findFurthestReachableFloorTile(GameMap map, int startX, int startY) {
        int width = map.getWidth();
        int height = map.getHeight();
        boolean[][] visited = new boolean[width][height];
        int[][] distance = new int[width][height];

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});
        visited[startX][startY] = true;
        distance[startX][startY] = 0;

        int maxDist = 0;
        int[] furthestTile = new int[]{startX, startY};

        int[][] directions = {
            {0, -1}, {1, 0}, {0, 1}, {-1, 0}
        };

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx >= 0 && nx < width && ny >= 0 && ny < height &&
                    !visited[nx][ny] &&
                    map.getTile(nx, ny).getType() == TileType.FLOOR) {

                    visited[nx][ny] = true;
                    distance[nx][ny] = distance[x][y] + 1;
                    queue.add(new int[]{nx, ny});

                    if (distance[nx][ny] > maxDist) {
                        maxDist = distance[nx][ny];
                        furthestTile[0] = nx;
                        furthestTile[1] = ny;
                        }
                }
            }      
        }
        //System.out.println("------------further exit is  on x="+furthestTile[0]+ " y= "+ furthestTile[1] );
        
        return furthestTile;
    }
}