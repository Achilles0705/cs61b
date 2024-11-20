# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer:别的都相同，教程把x和y合并为了一个position。打印一列六边形抽象为方法能够有效简化代码。

-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer:微能，大概40%吧。六边形是房间与走廊，镶嵌是让它们完美合在一起而不重叠

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer:像proj0-2048那样，用while获取外部方向键输入，在用随机生成的方式按区块生成地图，每个场景有固定的区块大小

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer:场景图案不同，功能不同（？）都是区块组成
