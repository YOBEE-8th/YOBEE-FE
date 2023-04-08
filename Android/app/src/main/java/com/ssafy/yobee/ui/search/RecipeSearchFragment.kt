package com.ssafy.yobee.ui.search

import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import com.ssafy.domain.model.recipe.RecipeDomainModel
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentRecipeSearchBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.search.adapter.AutoCompleteAdapter
import com.ssafy.yobee.ui.search.adapter.RecipeSearchListAdapter
import com.ssafy.yobee.util.common.ViewUtils
import com.ssafy.yobee.util.extension.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeSearchFragment :
    BaseFragment<FragmentRecipeSearchBinding>(R.layout.fragment_recipe_search) {
    private var recyclerViewState: Parcelable? = null
    private lateinit var recipeSearchListAdapter: RecipeSearchListAdapter
    private lateinit var searchHistoryAdapter: ArrayAdapter<String>
    private val recipeSearchViewModel: RecipeSearchViewModel by navGraphViewModels(R.id.recipeSearchFragmentAndBottomSheet) {
        defaultViewModelProviderFactory
    }
    private val searchHistoryViewModel: SearchHistoryViewModel by viewModels()

    override fun initView() {
        initToolbar()
        initAdapter()
        binding.recipeSearchFragment = this
        setNoItemView(recipeSearchViewModel.searchedRecipeList.value?.value ?: emptyList())
        with(binding) {

            tlSearchKeyword.setStartIconOnClickListener {
                searchHistoryViewModel.addSearchHistory(actvRecipeSearchKeyword.text.toString())
                this@RecipeSearchFragment.recipeSearchViewModel.setKeyword(actvRecipeSearchKeyword.text.toString())
                it.hideKeyboard()
            }

            actvRecipeSearchKeyword.setOnFocusChangeListener { v, hasFocus ->
                (v as AutoCompleteTextView).showDropDown()
            }

            actvRecipeSearchKeyword.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val keyword = v.text.toString()
                    this@RecipeSearchFragment.recipeSearchViewModel.run {
                        setKeyword(keyword)
                    }
                    this@RecipeSearchFragment.searchHistoryViewModel.addSearchHistory(keyword)
                }
                v.hideKeyboard()
                false
            }
        }
    }

    private fun initAdapter() {
        recipeSearchListAdapter = RecipeSearchListAdapter().apply {
            addLikeRecipeButtonClickListener { recipeId ->
                recipeSearchViewModel.changeRecipeLikeStatus(recipeId)
            }
            addRecipeClickListener { recipeId, recipeTitle ->
                recyclerViewState = ViewUtils.saveRecyclerViewState(binding.rvRecipeSearch)
                navigate(
                    RecipeSearchFragmentDirections.actionRecipeSearchFragmentToRecipeDetailGraph(
                        recipeTitle, recipeId
                    )
                )
            }
        }
        binding.rvRecipeSearch.adapter = recipeSearchListAdapter

        searchHistoryAdapter =
            AutoCompleteAdapter(requireContext(), ArrayList()).apply {
                deleteKeyWordClickListener {
                    searchHistoryViewModel.deleteSearchHistory(
                        searchHistoryViewModel.searchHistoryList.value!!.value ?: emptyList(), it
                    )
                    binding.actvRecipeSearchKeyword.dismissDropDown()
                }
                keywordClickListener {
                    binding.actvRecipeSearchKeyword.setText(it)
                    binding.actvRecipeSearchKeyword.dismissDropDown()
                    recipeSearchViewModel.setKeyword(it)
                    requireView().hideKeyboard()
                }
            }
        binding.actvRecipeSearchKeyword.setAdapter(searchHistoryAdapter)

    }

    override fun initViewModels() {
        initObservers()
    }

    fun onSortButtonClick() {
        navigate(RecipeSearchFragmentDirections.actionRecipeSearchFragmentToSearchSortBottomSheetFragment())
    }

    fun onOrderButtonClick() {
        recipeSearchViewModel.setOrder()
    }

    fun onIsAiCheckBoxClick() {
        recipeSearchViewModel.setIsAI()
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.layoutToolbar.tbToolbar
        // arg : 타이틀, 백버튼 유무, 백버튼 눌렀을 때 수행할 동작
        toolbar.initToolbar(getString(R.string.title_search), true) {
            navigate(RecipeSearchFragmentDirections.actionRecipeSearchFragmentPop())
        }
    }

    fun initObservers() {
        with(recipeSearchViewModel) {
            keyword.observe(viewLifecycleOwner) {
                getSearchedRecipeList()
            }
            sortBy.observe(viewLifecycleOwner) {
                getSearchedRecipeList()
                binding.recipeSearchViewModel = recipeSearchViewModel
            }
            order.observe(viewLifecycleOwner) {
                getSearchedRecipeList()
                binding.recipeSearchViewModel = recipeSearchViewModel
            }
            isAI.observe(viewLifecycleOwner) {
                getSearchedRecipeList()
                binding.recipeSearchViewModel = recipeSearchViewModel
            }
            searchedRecipeList.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Error -> {
                        dismissLoadingDialog()
                        navigate(RecipeSearchFragmentDirections.actionRecipeSearchFragmentToErrorFragment())
                    }
                    is ViewState.Loading -> {
                        showLoadingDialog()
                    }
                    is ViewState.Success -> {
                        binding.llRecipeSearchResult.visibility = View.VISIBLE
                        dismissLoadingDialog()
                        if (response.value!!.isEmpty()) {
                            setNoItemView(response.value!!)
                        } else {
                            setNoItemView(response.value!!)
                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(100)
                            binding.rvRecipeSearch.scrollToPosition(0)
                        }
                    }
                }
            }

        }
        with(searchHistoryViewModel) {
            getSearchHistory()
            searchHistoryList.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Error -> {
                        requireContext().showToast(response.message!!)
                    }
                    is ViewState.Loading -> {}
                    is ViewState.Success -> {
                        searchHistoryAdapter.clear()
                        searchHistoryAdapter.addAll(response.value!!)
                        searchHistoryAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun setNoItemView(result: List<RecipeDomainModel>) {
        binding.apply {
            if (result.isEmpty()) {
                layoutNoItem.clNoItem.visibility = View.VISIBLE
                layoutNoItem.tvNoItem.text = getString(R.string.title_no_item_search)
                rvRecipeSearch.visibility = View.GONE
            } else {
                binding.llRecipeSearchResult.visibility = View.VISIBLE
                layoutNoItem.clNoItem.visibility = View.GONE
                rvRecipeSearch.visibility = View.VISIBLE
                recipeSearchListAdapter.submitList(result)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (recyclerViewState != null) {
            ViewUtils.setSavedRecyclerViewState(recyclerViewState, binding.rvRecipeSearch)
        }
    }
}