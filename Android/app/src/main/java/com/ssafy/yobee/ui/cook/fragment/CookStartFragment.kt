package com.ssafy.yobee.ui.cook.fragment

import android.content.Intent
import android.content.pm.ActivityInfo
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
import android.speech.tts.TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
import android.speech.tts.Voice
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentCookStartBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.common.CommonDialog
import com.ssafy.yobee.ui.cook.model.CookProgressViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val TAG = "CookStartFragment_요비"

@AndroidEntryPoint
class CookStartFragment : BaseFragment<FragmentCookStartBinding>(R.layout.fragment_cook_start) {
    private lateinit var tts: TextToSpeech
    private val args: CookStartFragmentArgs by navArgs()
    private lateinit var yobeeSpinnerAdapter: ArrayAdapter<String>
    private val cookProgressViewModel by navGraphViewModels<CookProgressViewModel>(R.id.cookProgressGraph) {
        defaultViewModelProviderFactory
    }
    var voice = ""

    override fun initView() {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        initToolbar()
        initRecipeValue()
        initSpinner()
        initButtons()
        initTTS()

    }

    private fun initTTS() {
        tts = TextToSpeech(requireContext(), { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.KOREAN
            }
        }, "com.google.android.tts")
    }

    private fun initButtons() {
        binding.apply {
            btnCookStartGuide.setOnClickListener {
                navigate(CookStartFragmentDirections.actionCookStartFragmentToGuideFragment())
            }
            btnCookStartStart.setOnClickListener {
                if (voice == "유리") {
                    checkSelectedVoiceIsExist("유리")

                } else if (voice == "두리") {
                    checkSelectedVoiceIsExist("두리")
                }
            }

        }
    }

    fun downloadTTSData() {
        val installIntent = Intent()
        installIntent.action = ACTION_INSTALL_TTS_DATA
        startActivity(installIntent)
    }

    override fun initViewModels() {
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.layoutToolbar.tbToolbar
        toolbar.initToolbar("요리하기", true) {
            navigate(CookStartFragmentDirections.actionCookStartFragmentPop())
        }
    }

    private fun initRecipeValue() {
        binding.apply {
            tvCookStartRecipe.text = args.recipeTitle
            Glide.with(requireContext())
                .load(args.recipeImg)
                .placeholder(R.drawable.shimmer)
                .into(ivCookStartRecipe)
        }
    }

    private fun initSpinner() {
        val voiceArray = arrayListOf("유리", "두리")
        yobeeSpinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, voiceArray)
        binding.spinnerCookStartYobee.apply {
            adapter = yobeeSpinnerAdapter

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    voice = parent?.getItemAtPosition(position).toString()
                    cookProgressViewModel.setVoiceName(voice)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }

    private fun checkSelectedVoiceIsExist(voiceName: String) {
        val installIntent = Intent().apply {
            action = ACTION_INSTALL_TTS_DATA
        }
        val voiceCode: String = if (voiceName == "유리") {
            "ko-kr-x-ism-local"
        } else {
            "ko-KR-language"
        }
        var voiceExist = false

        for (voice: Voice in tts.voices) {
            if (voice.name == voiceCode) {
                voiceExist = true
                break
            }
        }

        if (voiceExist) {
            cookProgressViewModel.apply {
                val checkIntent = Intent()
                checkIntent.action = ACTION_CHECK_TTS_DATA
                setVoiceCode(voiceCode)
                setRecipeId(args.recipeId)
            }
            val dialog =
                CommonDialog(requireContext(), "요리 비서가 필요하다면\n${voiceName}를 불러주세요", "확인") {
                    navigate(CookStartFragmentDirections.actionCookStartFragmentToCookProgressFragment())
                }
            showDialog(dialog, viewLifecycleOwner)
        } else {
            val dialog =
                CommonDialog(requireContext(), "기기에 선택하신 목소리가 없습니다.\n다운로드 받으시겠습니까?", "확인") {
                    startActivity(installIntent)
                }
            showDialog(dialog, viewLifecycleOwner)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (tts != null) {
            tts.stop()
            tts.shutdown()
        }
    }
}