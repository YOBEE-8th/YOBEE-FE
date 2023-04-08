package com.ssafy.yobee.ui.cook.fragment


import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.view.View
import android.view.WindowInsets
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.navGraphViewModels
import androidx.viewpager2.widget.ViewPager2
import com.gun0912.tedpermission.PermissionListener
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.alarm.AlarmFunctions
import com.ssafy.yobee.databinding.FragmentCookProgressBinding
import com.ssafy.yobee.ui.MainActivity
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.cook.AnswerDialog
import com.ssafy.yobee.ui.cook.adapter.CookProgressSlideAdapter
import com.ssafy.yobee.ui.cook.model.CookProgressViewModel
import com.ssafy.yobee.util.common.NotificationUtils
import com.ssafy.yobee.util.common.SpeechUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "CookProgressFragment_요비"

@AndroidEntryPoint
class CookProgressFragment :
    BaseFragment<FragmentCookProgressBinding>(R.layout.fragment_cook_progress) {
    private lateinit var cookProgressSlideAdapter: CookProgressSlideAdapter
    private val cookProgressViewModel by navGraphViewModels<CookProgressViewModel>(R.id.cookProgressGraph) {
        defaultViewModelProviderFactory
    }

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognitionListener: RecognitionListener
    private lateinit var speechIntent: Intent
    private lateinit var tts: TextToSpeech

    private lateinit var commandDialog: AnswerDialog

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun initView() {
        binding.cookProgressFragment = this@CookProgressFragment

        requireActivity().requestedOrientation =
            if (Build.VERSION.SDK_INT < 9) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        // Activity의 상태 표시줄 숨기기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars());
        } else {
            requireActivity().window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        initAdapter()
        initViewPager()
        setListener()
        initTTS()

    }

    private fun initTTS() {
        tts = TextToSpeech(requireContext(), TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.KOREAN
                tts.voice = Voice(
                    cookProgressViewModel.voiceCode,
                    Locale.KOREAN,
                    Voice.QUALITY_HIGH,
                    Voice.LATENCY_LOW,
                    false,
                    null
                )
            } else {
                requireContext().showToast("TTS 초기화 실패")
            }
        }, "com.google.android.tts")
    }

    private fun initViewPager() {
        binding.apply {
            vpCookProgress.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    cookProgressViewModel.setCurrentPosition(position)
                    pivCookProgress.selection = position

                    if (position == cookProgressSlideAdapter.itemCount - 1) {
                        if (tts.isSpeaking) tts.stop()
                        pivCookProgress.visibility = View.GONE
                    } else {
                        tts.speak(
                            cookProgressViewModel.cookProgressList.value!!.value!![position].description,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                        pivCookProgress.visibility = View.VISIBLE
                    }

                }
            })
        }
    }

    private fun initAdapter() {
        cookProgressSlideAdapter =
            CookProgressSlideAdapter(parentFragmentManager, lifecycle).apply {

            }
        binding.vpCookProgress.adapter = cookProgressSlideAdapter
    }

    override fun initViewModels() {
        cookProgressViewModel.getCookProgressList()
        initGetAnswerViewModel()
        initObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (tts != null) {
            tts.shutdown()
        }
    }

    private fun initObserver() {
        with(cookProgressViewModel) {
            timerTtsText.observe(viewLifecycleOwner) {
                if (!tts.isSpeaking) {
                    tts.speak(it, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
            cookProgressList.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Error -> {
                        navigate(CookProgressFragmentDirections.actionCookProgressFragmentToErrorFragment())
                    }
                    is ViewState.Loading -> {}
                    is ViewState.Success -> {
                        response.value!!.map {
                            cookProgressSlideAdapter.addFragment(CookProgressStepFragment(it))
                        }
                        cookProgressSlideAdapter.addFragment(CookFinishFragment())
                        binding.pivCookProgress.count = cookProgressSlideAdapter.itemCount
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initSpeech()
    }

    override fun onStop() {
        super.onStop()
        speechRecognizer.destroy()
        if (tts.isSpeaking) tts.stop()
    }

    // 음성인식 초기화
    private fun initSpeech() {
        SpeechUtils.getPermission(object : PermissionListener {
            override fun onPermissionGranted() {
                speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                speechIntent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                speechIntent.putExtra(
                    RecognizerIntent.EXTRA_CALLING_PACKAGE, requireContext().packageName
                )
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")

                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
                speechRecognizer.setRecognitionListener(recognitionListener)
                speechRecognizer.startListening(speechIntent)
                cookProgressViewModel.setSttStatusValue(true)
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                requireContext().showToast(resources.getString(R.string.content_permission_denied))
            }
        })
    }

    // 조리과정 Q&A 통신
    private fun getAnswer(message: String) {
        cookProgressViewModel.getAnswer(message)
    }

    private fun initGetAnswerViewModel() {
        cookProgressViewModel.getAnswerLiveData.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    Log.d(TAG, "음성 인식한 결과값 통신중")
                }
                is ViewState.Success -> {
                    when (response.value!!.type) {
                        "0" -> { // 화면 이동
                            when (response.value!!.text) {
                                "0" -> { // 처음 페이지로 이동
                                    if (binding.vpCookProgress.currentItem == 0) requireContext().showToast(
                                        resources.getString(R.string.content_answer_first_page)
                                    )
                                    else binding.vpCookProgress.currentItem = 0
                                }
                                "1" -> { // 이전 페이지로 이동
                                    if (binding.vpCookProgress.currentItem == 0) requireContext().showToast(
                                        resources.getString(R.string.content_answer_first_page)
                                    )
                                    else binding.vpCookProgress.currentItem -= 1
                                }
                                "2" -> { // 다음 페이지로 이동
                                    if (binding.vpCookProgress.currentItem == cookProgressSlideAdapter.itemCount - 2) requireContext().showToast(
                                        resources.getString(R.string.content_answer_last_page)
                                    )
                                    else binding.vpCookProgress.currentItem += 1
                                }
                                "3" -> { // 마지막 페이지로 이동
                                    if (binding.vpCookProgress.currentItem == cookProgressSlideAdapter.itemCount - 2) requireContext().showToast(
                                        resources.getString(R.string.content_answer_last_page)
                                    ) else binding.vpCookProgress.currentItem =
                                        cookProgressSlideAdapter.itemCount - 2
                                }
                                "4" -> {
                                    tts.speak(
                                        cookProgressViewModel.cookProgressList.value!!.value!![binding.vpCookProgress.currentItem].description,
                                        TextToSpeech.QUEUE_FLUSH,
                                        null,
                                        null
                                    )
                                }
                            }
                            speechRecognizerStart()
                        }
                        "1" -> { // 질문 답변
                            val answerDialog = AnswerDialog(requireContext(), response.value!!.text)
                            answerDialog.show()
                            tts.speak(response.value!!.text, TextToSpeech.QUEUE_FLUSH, null, null)
                            CoroutineScope(Dispatchers.Main).launch {
                                while (tts.isSpeaking) {
                                    delay(100)
                                }
                                delay(2000)
                                answerDialog.dismiss()
                                speechRecognizer.startListening(speechIntent)
                            }

                            answerDialog.setOnDismissListener {
                                if (tts.isSpeaking) {
                                    tts.stop()
                                }
                            }
                        }
                        "2" -> {
                            NotificationUtils.getPermission(object : PermissionListener {
                                override fun onPermissionGranted() {
                                    cookProgressViewModel.apply {
                                        if(cookProgressList.value?.value?.get(currentPosition)?.timer == 0){
                                            return
                                        }
                                        requireContext().showToast("타이머가 설정되었습니다.")
                                        val alarmFunctions = AlarmFunctions(requireContext())
                                        alarmFunctions.callAlarm(cookProgressList.value?.value?.get(currentPosition)?.timer!!,
                                            System.currentTimeMillis().toInt(),
                                            "요비 타이머")
                                        cookProgressViewModel.setTimerTtsText("타이머가 설정되었습니다.")
                                        Log.d(TAG, "onPermissionGranted: ")
                                    }

                                }

                                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                                    requireContext().showToast("권한을 허용해주세요.")
                                    Log.d(TAG, "onPermissionDenied: ")
                                }
                            })
                            speechRecognizerStart()
                        }
                        "3" -> { // 음성 볼륨 조절
                            val mAudioManager: AudioManager = mainActivity.getSystemService(AUDIO_SERVICE) as AudioManager
                            response.value!!.text.let { text ->
                                val currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                                when(text){
                                    "up" -> {
                                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume+4,AudioManager.FLAG_SHOW_UI)
                                    }
                                    "down" -> {
                                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume-4,AudioManager.FLAG_SHOW_UI)
                                    }
                                }
                            }
                            speechRecognizerStart()
                        }
                    }
                    Log.d(TAG, "음성 인식 통신 성공 : ${response.value!!.type} ${response.value!!.text}")
                }
                is ViewState.Error -> {
                    requireContext().showToast(response.message!!)
                    Log.d(TAG, "음성 인식 통신 실패")
                    speechRecognizerStart()
                }
            }
        }
    }

    // 음성 인식 리스너 등록
    private fun setListener() {
        recognitionListener = object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                cookProgressViewModel.setSttStatusValue(true)
            }

            override fun onBeginningOfSpeech() {

            }

            override fun onRmsChanged(rmsdB: Float) {

            }

            override fun onBufferReceived(buffer: ByteArray?) {

            }

            override fun onEndOfSpeech() {
                cookProgressViewModel.setSttStatusValue(false)
            }

            override fun onError(error: Int) {
                var message = ""
                when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> message =
                        resources.getString(R.string.content_error_audio)
                    SpeechRecognizer.ERROR_CLIENT -> Log.d(
                        TAG, "ERROR_CLIENT: ${resources.getString(R.string.content_error_client)}"
                    )
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> message =
                        resources.getString(R.string.content_error_insufficient_permissions)
                    SpeechRecognizer.ERROR_NETWORK -> message =
                        resources.getString(R.string.content_error_network)
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> message =
                        resources.getString(R.string.content_error_network_time_out)
                    SpeechRecognizer.ERROR_NO_MATCH -> Log.d(
                        TAG,
                        "ERROR_NO_MATCH: ${resources.getString(R.string.content_error_no_match)}"
                    )
                    SpeechRecognizer.ERROR_SERVER -> Log.d(
                        TAG, "ERROR_SERVER: ${resources.getString(R.string.content_error_server)}"
                    )
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> message =
                        resources.getString(R.string.content_error_speech_time_out)
                    else -> message = resources.getString(R.string.content_error_other)
                }
                if (message.isNotEmpty()) requireContext().showToast(message)
                speechRecognizerStart()
            }

            override fun onResults(results: Bundle?) {
                var matches: ArrayList<String> =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>

                if (cookProgressViewModel.isKeywordInActivate) { // 키워드 없이 말하는게 활성화된 상태
                    getAnswer(matches[0])
                    commandDialog.dismiss()

                } else {
                    if (!tts.isSpeaking) {
                        if (matches[0].contains(cookProgressViewModel.voiceName)) { // 특정 단어 인식하면 요청
                            val question = matches[0].split(" ")
                            val sb = StringBuilder()

                            out@ for (i in question.size-1 downTo 0) {
                                if (question[i].contains(cookProgressViewModel.voiceName)) {
                                    for (j in i until question.size) sb.append("${question[j]} ")
                                    break@out
                                }
                            }

                            getAnswer(sb.toString())
                        } else {
                            speechRecognizerStart()
                        }
                    } else {
                        speechRecognizerStart()
                    }
                }

                cookProgressViewModel.setKeywordExist(false)
                Log.d(TAG, "onResults: ${matches[0]}")
            }

            override fun onPartialResults(partialResults: Bundle?) {

            }

            override fun onEvent(eventType: Int, params: Bundle?) {

            }

        }
    }

    // 키워드 명령어 비활성화
    fun inActivateKeyWord() {
        if (tts.isSpeaking) {
            tts.stop()
        }

        commandDialog =
            AnswerDialog(requireContext(), resources.getString(R.string.content_question_help))
        commandDialog.show()
        cookProgressViewModel.setKeywordExist(true)
    }

    fun speechRecognizerStart() = CoroutineScope(Dispatchers.Main).launch {
        delay(1000)
        speechRecognizer.startListening(speechIntent)
        cookProgressViewModel.setSttStatusValue(true)
    }
}