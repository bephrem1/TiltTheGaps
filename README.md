# TiltTheGaps
"Flappy Bird"-like Game Utilizing The Android Gyroscope For Unique Game Modes<br />
By: Benyam Ephrem<br />
Date Created: Around 2015<br />

History: This is a game created by me when I was in the 10th grade in high school. I had no experience coding and this
was around the time Flappy Bird came out. I decided to teach myself how to program apps and taught myself Java in 1 month
and learned Android development soon after with TeamTreehouse. After this game and another I fell off on Android developement
but since have restarted my ferverent study of programming again.

Explanation: Flappy Bird is a game where you tap the screen and try to get the bird through the holes in pipes approaching
the bird. This game I created takes a similar yet different approach. It utilizes the gyroscope of the Android device to manipulate a 
ball. It simply uses a redraw function and every redraw conditions are checked to advance and facilitate certain game modes.
_________________________________________________________________

There are 3 game modes:

Traditional - This is the Flappy Bird inspired game mode that I first created. You tilt the phone through upcoming pipes and each pipe you pass you get a point and hear a "pop" sound to signal a successful pass. If the ball does not clear the gap in the pipe fully and touches the pipe (represented by 2 vertical rectangles with a gap between them) the player loses and is sent to a game over screen. This game mode cycles 3 rectangle objects that randomize height each time the reach the full left of the screen giving an apprerance of smooth motion of on-coming obstacles.

Chase Mode - This mode is my favorite and the one I am the most proud of. It employs a pseudo-chase mechanism in which you start out with your ball in the center. Then you exit the starting "bubble" and begin to get chased by an evil red ball which if you touch it you lose. You must collect the bigger balls that randomly appear during the chase and you have to avoid the red ball at all times! This mode was very technical to program but it's core is the pseudo-chase mechanism where the enemy ball is always a certain amount of positional array indecies behind your ball and the more points you get the tighter the chase gets!

Shift Mode - This is the more creative game mode of the game and is sort of like the classic game Pong. You start in the center and tilt out of the starting "bubble" circle to begin the game. On a random far side of the screen 3 rectangles of equal length will spawn. Each rectangle is 1/3 of the screen tall and very thin in width so it just hugs the edge and they are stacked on top of another. The player has to collide the ball by tilting the screen with the rectangle of the same color as the ball to earn a point. Every point the ball color changes and the time the player gets to get to the other side to hit the rectangle of the same color as the ball gets less and less. So it is an intense game mode where reaction time and motor skills are key to getting a high score. If you collide the ball with a rectangle of a different color from it then the player loses.

_________________________________________________________________

Use of Code and Testing: The code in this repository is simply the essential source code needed to replicate the game in another Android Studio Project. Simply port the code into Android Studio and you can run it within' the provided Android Studio framework (after editing naming issues with packages)

This was the first program I ever made primarily myself (despite consulting Stack Overflow often for small issues and bugs) and I am very proud of it. I didn't have a designer to help me make visual assets to make the game more visually appealing, but the code fascinates me and the fact that I made something that works (working through seemingly impossible bugs) is quite satisfying.
