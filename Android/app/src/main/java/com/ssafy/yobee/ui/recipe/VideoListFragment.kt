package com.ssafy.yobee.ui.recipe

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentVideoListBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.recipe.adapter.VideoListAdapter
import com.ssafy.yobee.ui.recipe.model.VideoModel
import com.ssafy.yobee.util.common.ViewUtils
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "VideoListFragment_요비"

@AndroidEntryPoint
class VideoListFragment : BaseFragment<FragmentVideoListBinding>(R.layout.fragment_video_list) {

    private val recipeViewModel: RecipeViewModel by navGraphViewModels(R.id.recipeDetailGraph) {
        defaultViewModelProviderFactory
    }
    private lateinit var videoListAdapter: VideoListAdapter
    private lateinit var videoModel: VideoModel
    var isCalled = false
    var height = -1

    override fun onResume() {
        super.onResume()
        isCalled = false
    }

    override fun initView() {
    }

    override fun initViewModels() {
        initRecipeViewModel()
    }

    private fun initRecipeViewModel() {
        recipeViewModel.apply {
            videoLiveData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Success -> {
                        dismissLoadingDialog()
                        videoModel = response.value!!
                        initRecyclerView()
                    }
                    is ViewState.Error -> {
                        dismissLoadingDialog()
                        Log.d(TAG, "레시피 관련 영상 에러 : ${response.message}")
                        if (findNavController().currentDestination?.id == R.id.recipeDetailFragment) {
                            navigate(RecipeDetailFragmentDirections.actionRecipeDetailFragmentToErrorFragment())
                        }
                    }
                    is ViewState.Loading -> {
                        showLoadingDialog()
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        videoListAdapter = VideoListAdapter(requireContext(), videoModel.videos).apply {
            setItemClickListener(object : VideoListAdapter.ItemClickListener {
                override fun onClick(view: View, position: Int, videoId: String) {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=$videoId")
                    )
                    startActivity(intent)
                }
            })
        }


        binding.apply {
            rvVideo.adapter = videoListAdapter
            rvVideo.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                recipeViewModel.setYoutubeHeight(ViewUtils.getRecyclerViewItemHeight(rvVideo, 10))

            }
        }
    }
}