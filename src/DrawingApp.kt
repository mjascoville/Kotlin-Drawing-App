import javax.swing.*
import java.awt.*
import java.awt.event.*
import kotlin.math.sqrt

//These data classes store and manage my shape properties
data class Line(val startX: Int, val startY: Int, val endX: Int, val endY: Int, val color: Color, val thickness: Float)
data class Circle(val centerX: Int, val centerY: Int, val radius: Int, val color: Color, val thickness: Float)

class DrawingApp : JFrame() {
    private val shapes = mutableListOf<Any>() //This list is where the shapes are all stored
    private val tools = listOf("Line", "Circle") //Here are the drawing tools we can use
    private var currentToolIndex: Int = 0 //Here is where the current tool is stored, and we start off with the line tool
    private var currentTool: String = tools[currentToolIndex]
    private var currentColor: Color = Color.BLACK //Starting color is initialized to black
    //These set the starting points of our shapes when we draw
    private var startX: Int = 0
    private var startY: Int = 0

    //Setting up our window and drawing space
    init {
        title = "Drawing App"
        size = Dimension(800, 600)
        defaultCloseOperation = EXIT_ON_CLOSE

        //Here is the main loop where the shapes are called
        val drawingPanel = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                for (shape in shapes) {
                    when (shape) {
                        is Line -> drawLine(shape, g)
                        is Circle -> drawCircle(shape, g)
                    }
                }
            }
        }

        //MouseListener is a handy way for us to track mouse inputs
        drawingPanel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) { //Pressing the mouse button records the initial coordinates
                startX = e.x
                startY = e.y
            }

            override fun mouseReleased(e: MouseEvent) { //Releasing the mouse button records the end point and draws either a line or circle
                when (currentTool) {
                    "Line" -> {
                        val line = Line(startX, startY, e.x, e.y, currentColor, 2f)
                        shapes.add(line) //We're adding this shape to our shapes list
                    }
                    "Circle" -> {
                        val radius = sqrt(((e.x - startX) * (e.x - startX) + (e.y - startY) * (e.y - startY)).toDouble()).toInt()
                        val circle = Circle(startX, startY, radius, currentColor, 2f)
                        shapes.add(circle)
                    }
                }
                drawingPanel.repaint() //This redraws the screen according to our new shapes list
            }
        })

        drawingPanel.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyChar) {
                    'x' -> { //Cycles through the current tool
                        currentToolIndex = (currentToolIndex + 1) % tools.size
                        currentTool = tools[currentToolIndex]
                        println("Current tool: $currentTool")
                    }
                    'r' -> { //Changes the color to red
                        currentColor = Color.RED
                        println("Current color: Red")
                    }
                    'b' -> { //Changes the color to black
                        currentColor = Color.BLACK
                        println("Current color: Black")
                    }
                    'z' -> { //Undo function. This is cool, because it actually goes into the shapes list, removes the last item, and then redraws the screen
                        try {
                            shapes.removeLast()
                        } finally {
                            drawingPanel.repaint()
                        }
                    }
                }
            }
        })

        //This just lets us listen for key events
        drawingPanel.isFocusable = true
        drawingPanel.requestFocusInWindow()

        add(drawingPanel)
        isVisible = true
    }

    //Here are our drawing methods. This sets the color, thickness, etc.
    private fun drawLine(line: Line, g: Graphics) {
        g.color = line.color
        (g as Graphics2D).stroke = BasicStroke(line.thickness)
        g.drawLine(line.startX, line.startY, line.endX, line.endY)
    }

    private fun drawCircle(circle: Circle, g: Graphics) {
        g.color = circle.color
        (g as Graphics2D).stroke = BasicStroke(circle.thickness)
        g.drawOval(circle.centerX - circle.radius, circle.centerY - circle.radius, circle.radius * 2, circle.radius * 2)
    }
}

fun main() {
    SwingUtilities.invokeLater { DrawingApp() }
}