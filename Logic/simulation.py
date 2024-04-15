from random import choice
from time import sleep

from oht_move_simulation import Direction, OHT, Grid
from start_end_permutation import generate_delivery_order


if __name__ == '__main__':
    equipment_dict = {"A": 9, "B": 72, "C": 89, "D": 53}

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

    ways_list = [node for key, node in grid.node_dict.items() if node.directions[0] != Direction.BLOCK and node.directions[0] != Direction.KEEP_GOING]

    # start_node_id = 1
    # pickup_node_id = 32
    # target_node_id = 75

    pickup_node, target_node = generate_delivery_order()

    print(pickup_node, target_node)
    pickup_node_id = equipment_dict.get(pickup_node)
    target_node_id = equipment_dict.get(target_node)
    OHT_location = choice(ways_list)

    print("====동작한다잉====")
    print("시작지점 :", (OHT_location.x, OHT_location.y))
    print("pickup지점 : ", (grid.node_dict.get(pickup_node_id).x, grid.node_dict.get(pickup_node_id).y))
    print("target지점 : ", (grid.node_dict.get(target_node_id).x, grid.node_dict.get(target_node_id).y))
    print("================")
    oht = OHT('OHT001', grid, OHT_location.id, pickup_node_id, target_node_id)
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
        print("=================")

        sleep(0.5)