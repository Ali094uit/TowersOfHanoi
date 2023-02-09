package uit.andreas.towersofhanoi

import android.app.Activity
import android.content.ClipData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Parcelable
import android.os.PersistableBundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.RecyclerView


class MainActivity() : AppCompatActivity() {

    //Sørger for at rotasjon ikke resetter data

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    var counter = 0  //Konstant
    var clicker = 1  //Konstant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) //Content view fra mainLayout

        //Starter timer ved initialisering av app
        startTimerCounter()

        // onTouchlistener på Rød ring, blå ring og Oransje ring
        val redRing = findViewById<ImageView>(R.id.redRing)
        redRing.setOnTouchListener(MyTouchListener())

        val blueRing = findViewById<ImageView>(R.id.blueRing)
        blueRing.setOnTouchListener(MyTouchListener())

        val orangeRing = findViewById<ImageView>(R.id.orangeRing)
        orangeRing.setOnTouchListener(MyTouchListener())

        // Hver layout eller hvert tårn må håndtere onDrag, setter opp listener
        val towerOne = findViewById<LinearLayout>(R.id.tower1)
        towerOne.setOnDragListener(MyDragListener())

        val towerTwo = findViewById<LinearLayout>(R.id.tower2)
        towerTwo.setOnDragListener(MyDragListener())

        val towerThree = findViewById<LinearLayout>(R.id.tower3)
        towerThree.setOnDragListener(MyDragListener())

        //ResetButton
        val resetButton = findViewById<Button>(R.id.resetButton)
        resetButton.setOnClickListener {this
            if (savedInstanceState == null) {

                val intent = intent
                finish()
                startActivity(intent)
            }
        }

    }
    fun startTimerCounter() {
        val countTime = findViewById<TextView>(R.id.bruktTid)
        object : CountDownTimer(50000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countTime.text = "Tid i sekunder: ${counter.toString()}"
                counter++
            }
            override fun onFinish() {
                countTime.text = "${counter.toString()}"
            }
        }.start()
    }

    // Egen klasse som arver av OnTouchListener, der man overstyrer funksjon onTouch og setter opp selv
    inner class MyTouchListener : View.OnTouchListener {
        override fun onTouch(viewToBeDragged: View, motionEvent: MotionEvent): Boolean {

            val owner = viewToBeDragged.parent as LinearLayout
            val top = owner.getChildAt(0)

            //Drag starter når den blir "clicka" på dersom imgview ligger øverst det ligger andre ringer under
            return if (viewToBeDragged == top || owner.childCount == 1) {

                val data = ClipData.newPlainText("", "") //Visst man skal lage og ta med data
                val shadowBuilder = View.DragShadowBuilder(viewToBeDragged) //Lager shadow av viewet som dras
                viewToBeDragged.startDragAndDrop(data, shadowBuilder, viewToBeDragged, 0) //starter drag and drop
                viewToBeDragged.visibility = View.INVISIBLE //Setter view visible
                true

            } else {
                Toast.makeText(this@MainActivity, "Du kan bare flytte toppen", Toast.LENGTH_LONG).show()
                false //Dersom ikke ligger øverst skrives det ut en toast
            }
        }
    }

    inner class MyDragListener : View.OnDragListener { //Arver fra OnDragListener
        //De tre tårnene må håndtere drag-events

        var enterShape = getDrawable(R.drawable.tower_shape_droptarget)
        var normalShape = getDrawable(R.drawable.tower_shape)

        override fun onDrag(view: View, event: DragEvent): Boolean { //Overstyrer onDrag fra superklassen
            val action = event.action
            val draggedView = event.localState as View //Returnerer objektet som blir dratt og saver det
            val recieveContainer = view as LinearLayout //Container som mottar views (tårnene)
            var antallFlytt = findViewById<TextView>(R.id.antallFlytt) //Variabel som viser flytt i txtview

            when(action) { //Når view blir dratt så skjer følgende:
                DragEvent.ACTION_DRAG_STARTED -> {
                }
                DragEvent.ACTION_DRAG_ENTERED -> //View kommer inn i ny view, bakgrunn settes til entershape
                    view.setBackground(enterShape)
                DragEvent.ACTION_DRAG_EXITED -> //View har forlatt gammel view, bakgrunn settes til normalshape
                    view.setBackground(normalShape)

                DragEvent.ACTION_DROP -> { //Når det droppes skjer følgende:
                    val toTower = view as LinearLayout
                    val topElement: View? = toTower.getChildAt(0) ?: null

                    //Bildet eller Ringen som blir dratt, må hente ut width å sjekke mot underliggende
                    val draggedRing = event.localState as ImageView

                    if(topElement != null) { //Dersom ikke null(tomt tårn)
                        val draggedRingWidth = draggedRing.width //bredde til ring
                        val topElementWidth = topElement.width //bredde til øverste ring
                        if (draggedRingWidth >= topElementWidth) { //Dersom ring som dras større enn, false
                            return false
                        }
                    }
                    antallFlytt.text = "Antall flytt: ${clicker}"
                    clicker ++
                    draggedView.visibility = View.VISIBLE //Dersom null så kan ring dras på tårn og settes visible
                    val owner = draggedView.parent as ViewGroup
                    owner.removeView(draggedView) //Sletter ring fra forrige view
                    recieveContainer.addView(draggedView, 0) //Bruker index for å legge til øverst i container
                    if (owner.isEmpty()) {
                        return true
                    }

                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    draggedView.visibility = View.VISIBLE //Viser view
                    recieveContainer.background = normalShape //Container bakgrunn normalshape
                }
                else -> {}
            }
            return true

        }
    }
}








