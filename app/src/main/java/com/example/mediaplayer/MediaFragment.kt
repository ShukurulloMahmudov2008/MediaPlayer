package com.example.mediaplayer

import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediaplayer.databinding.FragmentMediaBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MediaFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentMediaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var root: View
    lateinit var music: Music
    var position: Int = 0
    var mediaPlayer: MediaPlayer? = null
    lateinit var handler: Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaBinding.inflate(layoutInflater, container, false)

        position = arguments?.getInt("position", -1)!!
        music = arguments?.getSerializable("music") as Music

        return root
    }

    override fun onResume() {
        super.onResume()


        if (position != -1) {
            mediaPlayer = null
            mediaPlayer = MediaPlayer.create(context, Uri.parse(MyData.list[position].musicPath))
            mediaPlayer?.start()
            binding.btnPause.background = resources.getDrawable(R.drawable.ic_pause)
            binding.seekbar.max = mediaPlayer?.duration!!
            handler = Handler(activity?.mainLooper!!)

            binding.txtAllMusicSize.text = MyData.list.size.toString()
            binding.txtNumberMusic.text = (position + 1).toString()
            if (MyData.list[position].imagePath != "") {
                val bm = BitmapFactory.decodeFile(MyData.list[position].imagePath)
                binding.imageMusic.setImageBitmap(bm)
//                root.image_music.imageView.setImageBitmap(bm)
            }

            binding.txtMusicArtist.text = MyData.list[position].author
            binding.txtMusicName.text = MyData.list[position].title

            binding.txtAllTimeMusic.text = milliSecondsToTimer(mediaPlayer?.duration!!.toLong())
        }
        if (mediaPlayer?.isPlaying!!) {
            handler.postDelayed(runnable, 100)
        }

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        binding.btnBack30.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer?.currentPosition!!.minus(30000))
        }
        binding.btnNext30.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer?.currentPosition!!.plus(30000))
        }

        binding.imageMenuMore.setOnClickListener {
            releaseMP()
            findNavController().popBackStack()
        }
        binding.btnPause.setOnClickListener {
            if (mediaPlayer?.isPlaying!!) {
                mediaPlayer?.pause()
                binding.btnPause.background = resources.getDrawable(R.drawable.ic_play)
            } else {
                mediaPlayer?.start()
                binding.btnPause.background = resources.getDrawable(R.drawable.ic_pause)
            }
        }
        binding.btnNextMusic.setOnClickListener {
            if (++position < MyData.list.size) {
                releaseMP()
                onResume()
            } else {
                position = 0
                releaseMP()
                onResume()
            }
        }
        binding.btnBackMusic.setOnClickListener {
            if (--position >= 0) {
                releaseMP()
                onResume()
            } else {
                position = MyData.list.size - 1
                releaseMP()
                onResume()
            }
        }
    }

    //app stop when music stop
    private fun releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.release()
                mediaPlayer = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        releaseMP()
    }

    private var runnable = object : Runnable {
        override fun run() {

            if (mediaPlayer != null) {
                binding.seekbar.progress = mediaPlayer?.currentPosition!!
                binding.txtMusicTimePosition.text =
                    milliSecondsToTimer(mediaPlayer?.currentPosition!!.toLong())
                if (binding.txtMusicTimePosition.text.toString() == binding.txtAllTimeMusic.text.toString()) {
                    releaseMP()
                    if (++position < MyData.list.size) {
                        releaseMP()
                        onResume()
                    } else {
                        position = 0
                        releaseMP()
                        onResume()
                    }
                }
                handler.postDelayed(this, 100)
            }
        }
    }

    fun milliSecondsToTimer(milliseconds: Long): String? {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"

        // return timer string
        return finalTimerString
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MediaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}