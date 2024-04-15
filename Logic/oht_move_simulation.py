from enum import Enum
from collections import deque
from time import sleep

class Direction(Enum):
    BLOCK = "BLOCK"
    KEEP_GOING = "KEEP_GOING"
    RIGHT = "RIGHT"
    DOWN = "DOWN"
    LEFT = "LEFT"
    UP = "UP"
    UP_LEFT = "UP_LEFT"
    UP_RIGHT = "UP_RIGHT"
    DOWN_LEFT = "DOWN_LEFT"
    DOWN_RIGHT = "DOWN_RIGHT"

class Node:
    def __init__(self, id, x, y):
        self.id = id
        self.x = x
        self.y = y
        self.directions = []  # 이 노드에서 출발 가능한 방향들
        self.enabled = True # 해당 노드에 OHT가 없는 경우 True
        self.isError = False

    def add_directions(self, directions):
        if isinstance(directions, list):
            self.directions.extend(directions)  # 리스트인 경우, 여러 방향 추가
        else:
            self.directions.append(directions)  # 단일 방향 추가

    def __str__(self):
        return f"Node(id={self.id}, {self.x}, {self.y}, Directions: {[dir.value for dir in self.directions]})"

class Grid:
    def __init__(self, grid_data):
        self.height = len(grid_data)
        self.width = len(grid_data[0]) if self.height > 0 else 0
        self.nodes = [[Node(x*self.width + y, x, y) for y in range(self.width)] for x in range(self.height)]
        self.node_dict = {}  # 노드 ID를 키로, 노드 객체를 값으로 하는 딕셔너리
        self.setup_nodes(grid_data)

    def setup_nodes(self, grid_data):
        for x in range(self.height):
            for y in range(self.width):
                current_node = self.nodes[x][y]
                directions = grid_data[x][y]
                current_node.add_directions(directions)  # 노드에 가능한 방향들 설정
                self.node_dict[current_node.id] = current_node  # 딕셔너리에 노드 저장
    
    def get_delta(self, direction):
        """방향에 따라 좌표 변화량을 반환합니다."""
        delta_mappings = {
            Direction.RIGHT: (0, 1),
            Direction.DOWN: (1, 0),
            Direction.LEFT: (0, -1),
            Direction.UP: (-1, 0),
            Direction.UP_LEFT: (-1, -1),
            Direction.UP_RIGHT: (-1, 1),
            Direction.DOWN_LEFT: (1, -1),
            Direction.DOWN_RIGHT: (1, 1),
            Direction.KEEP_GOING: (0, 0),  # KEEP_GOING은 현재 방향 유지, 상황에 따라 처리
            Direction.BLOCK: (0, 0)       # BLOCK는 이동 불가
        }
        return delta_mappings.get(direction, (0, 0))  # 기본값은 이동 없음

    def is_valid(self, x, y):
        """주어진 좌표가 격자 내부에 있는지 검증합니다."""
        return 0 <= x < self.height and 0 <= y < self.width

class OHT:
    def __init__(self, identifier, grid, start_node_id, pickup_node_id, target_node_id):
        self.identifier = identifier
        self.grid = grid
        self.location = grid.node_dict[start_node_id]
        self.pickup_node = grid.node_dict[pickup_node_id]
        self.target_node = grid.node_dict[target_node_id]
        self.path = deque()  
        self.current_direction = None
    
    
    def move(self):
        """path에서 다음 노드로 이동할 수 있다면 이동하고 현재 위치를 업데이트한다."""
        if not self.path:
            # print(f"{self.identifier} has no more moves to make.")
            return None
        # path 큐에서 다음 노드를 꺼내 현재 위치로 설정
        if self.path[0].isError or not self.path[0].enabled:
            # print(f"{self.identifier} cannot move.")
            return None
        else:
            self.location.enabled = True
            next_node = self.path.popleft()
            self.location = next_node
            self.location.enabled = False
            # print(f"{self.identifier} moved to {next_node}")
        return next_node
            

    def bfs_find_path(self):
        # 첫 번째 경로: 시작 노드에서 픽업 노드까지
        first_path = self.bfs_path(self.location, self.pickup_node)
        if not first_path:
            return None  # 픽업 노드에 도달할 수 없으면 실패
        
        # 두 번째 경로: 픽업 노드에서 타겟 노드까지
        second_path = self.bfs_path(self.pickup_node, self.target_node)
        if not second_path:
            return None  # 타겟 노드에 도달할 수 없으면 실패
        
        # 두 경로를 합침 (픽업 노드 중복 제거)
        self.path = deque(first_path + second_path[1:])  # 두 번째 경로의 첫 노드는 픽업 노드이므로 중복 제거
        return self.path
    
    def bfs_path(self, start_node, end_node):
        # 큐에는 시작 노드, 경로, 그리고 마지막 이동 방향을 포함합니다.
        queue = deque([(start_node, [start_node], None)])  # 마지막 인자는 last_direction
        visited = set([start_node])

        while queue:
            current_node, path, last_direction = queue.popleft()
            if current_node == end_node:
                return path  # 목표 노드에 도달한 경우, 경로 반환
            
            for direction in current_node.directions:
                # KEEP GOING 처리: 마지막 방향을 유지
                
                if direction == Direction.KEEP_GOING and last_direction:
                    direction = last_direction

                dx, dy = self.grid.get_delta(direction)
                nx, ny = current_node.x + dx, current_node.y + dy

                if self.grid.is_valid(nx, ny):
                    next_node = self.grid.nodes[nx][ny]
                    if next_node not in visited:
                        visited.add(next_node)

                        # 다음 노드, 현재 경로에 다음 노드 추가, 현재 방향을 last_direction으로 업데이트하여 큐에 추가
                        queue.append((next_node, path + [next_node], direction))

        return None  # 경로를 찾지 못한 경우
    
if __name__ == '__main__':

    # Grid 데이터를 이용한 Grid 객체 생성 및 노드 설정
    grid_data = [
        [Direction.BLOCK, Direction.DOWN_LEFT, Direction.LEFT, Direction.KEEP_GOING, Direction.KEEP_GOING,
         Direction.KEEP_GOING, [Direction.LEFT, Direction.DOWN_LEFT], Direction.LEFT, Direction.BLOCK],
        [Direction.DOWN, Direction.BLOCK, Direction.BLOCK, [Direction.UP_LEFT, Direction.RIGHT], Direction.KEEP_GOING,
         Direction.DOWN, Direction.BLOCK, Direction.BLOCK, Direction.UP_LEFT],
        [Direction.KEEP_GOING, Direction.BLOCK, Direction.BLOCK, Direction.UP, Direction.BLOCK, Direction.DOWN,
         Direction.BLOCK, Direction.BLOCK, Direction.KEEP_GOING],
        [Direction.DOWN_RIGHT, Direction.BLOCK, Direction.BLOCK, Direction.UP, Direction.KEEP_GOING,
         [Direction.DOWN_RIGHT, Direction.LEFT], Direction.BLOCK, Direction.BLOCK, Direction.UP],
        [Direction.BLOCK, Direction.RIGHT, [Direction.UP_RIGHT, Direction.RIGHT], Direction.KEEP_GOING,
         Direction.KEEP_GOING, Direction.KEEP_GOING, Direction.RIGHT, [Direction.UP_RIGHT, Direction.DOWN_RIGHT],
         Direction.BLOCK],
        [Direction.UP_RIGHT, Direction.BLOCK, Direction.BLOCK, Direction.BLOCK, Direction.BLOCK, Direction.BLOCK,
         Direction.BLOCK, Direction.BLOCK, Direction.DOWN],
        [Direction.UP, Direction.BLOCK, Direction.BLOCK, Direction.BLOCK, Direction.BLOCK, Direction.BLOCK,
         Direction.BLOCK, Direction.BLOCK, Direction.DOWN_LEFT],
        [Direction.BLOCK, [Direction.UP_LEFT, Direction.DOWN_LEFT], Direction.LEFT, Direction.KEEP_GOING,
         Direction.KEEP_GOING, Direction.KEEP_GOING, [Direction.LEFT, Direction.DOWN_LEFT], Direction.LEFT,
         Direction.BLOCK],
        [Direction.DOWN, Direction.BLOCK, Direction.BLOCK, Direction.UP_LEFT, Direction.BLOCK, Direction.DOWN,
         Direction.BLOCK, Direction.BLOCK, Direction.UP_LEFT],
        [Direction.DOWN_RIGHT, Direction.BLOCK, Direction.BLOCK, Direction.UP, Direction.BLOCK, Direction.DOWN_RIGHT,
         Direction.BLOCK, Direction.BLOCK, Direction.UP],
        [Direction.BLOCK, Direction.RIGHT, [Direction.RIGHT, Direction.UP_RIGHT], Direction.KEEP_GOING,
         Direction.KEEP_GOING, Direction.KEEP_GOING, Direction.RIGHT, Direction.UP_RIGHT, Direction.BLOCK]
    ]

    grid = Grid(grid_data)

    start_node_id = 1
    pickup_node_id = 32
    target_node_id = 75
    oht = OHT('OHT001', grid, start_node_id, pickup_node_id, target_node_id)
    path = oht.bfs_find_path()

    # 최단 경로 출력
    if path:
        print("Path found:", [f"(id: {node.id}, x: {node.x}, y: {node.y})" for node in path])
    else:
        print("No path found")

    while oht.move():
        # print(oht.location)
        # 각 Direction에 대응하는 화살표 또는 문자
        direction_arrows = {
            Direction.BLOCK: "🚫",
            Direction.KEEP_GOING: "ㅡ",
            Direction.RIGHT: "➡️",
            Direction.DOWN: "⬇️",
            Direction.LEFT: "⬅️",
            Direction.UP: "⬆️",
            Direction.UP_LEFT: "↖️",
            Direction.UP_RIGHT: "↗️",
            Direction.DOWN_LEFT: "↙️",
            Direction.DOWN_RIGHT: "↘️"
        }

        # 주어진 grid_data의 각 Direction 값을 화살표로 치환
        grid_arrows = []
        for x, row in enumerate(grid_data):
            arrow_row = []
            for y, item in enumerate(row):

                if x == oht.location.x and y == oht.location.y:
                    arrow_row.append("🚚")

                elif isinstance(item, list):
                    # 리스트 안의 여러 방향을 처리
                    combined_arrows = direction_arrows[item[0]]
                    arrow_row.append(combined_arrows)
                else:
                    # 단일 방향을 화살표로 치환
                    arrow_row.append(direction_arrows[item])
            grid_arrows.append(arrow_row)

        # 치환된 화살표 데이터 출력
        for row in grid_arrows:
            print(" ".join(row))
        print()

        sleep(0.5)

