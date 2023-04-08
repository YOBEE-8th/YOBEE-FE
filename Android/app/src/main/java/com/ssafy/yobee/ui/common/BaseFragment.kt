package com.ssafy.yobee.ui.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.ssafy.yobee.R

/** Fragment 대신 상속 */
abstract class BaseFragment<T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int,
) : Fragment() {

    // binding 객체
    private var _binding: T? = null
    val binding: T get() = _binding!!

    protected val navController: NavController
        get() = NavHostFragment.findNavController(this)

    lateinit var mLoadingDialog: LoadingDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mLoadingDialog = LoadingDialog()
    }

    /** 화면 inflate */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    /** view 생성 작업 */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        initView()
        initViewModels()
    }

    /** 프래그먼트 간 이동 */
    fun navigate(directions: NavDirections) {
        navController.navigate(directions)
    }

    /** 뒤로가기 */
    fun popBackStack() {
        navController.popBackStack()
    }

    /** view 초기화 작업 */
    abstract fun initView()

    /** viewModel 초기화 작업 */
    abstract fun initViewModels()

    /** 사용하지 않는다면 null 처리 */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun Toolbar.initToolbar(
        toolbarTitle: String,
        isBackAvailable: Boolean?,
        backAction: (() -> Unit)?,
    ) {
        this.title = toolbarTitle
        if (isBackAvailable == true) this.setNavigationIcon(R.drawable.ic_back)
        this.setNavigationOnClickListener {
            if (backAction != null) {
                backAction()
            }
        }
    }

    fun Context.showToast(message: String) {
        CustomToast.makeText(this, message)?.show()
    }

    fun showDialog(
        dialog: Dialog,
        lifecycleOwner: LifecycleOwner?,
        cancelable: Boolean = true,
        dismissHandler: (() -> Unit)? = null,
    ) {
        val targetEvent = if (cancelable) Lifecycle.Event.ON_STOP else Lifecycle.Event.ON_DESTROY
        val observer = LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
            if (event == targetEvent && dialog.isShowing) {
                dialog.dismiss()
                dismissHandler?.invoke()
            }
        }
        dialog.show()
        lifecycleOwner?.lifecycle?.addObserver(observer)
        dialog.setOnDismissListener { lifecycleOwner?.lifecycle?.removeObserver(observer) }
    }

    fun showLoadingDialog() {
        if (!mLoadingDialog.isAdded) {
            mLoadingDialog.show(requireFragmentManager(), "loader")
        }
    }

    fun dismissLoadingDialog() {
        if (mLoadingDialog.isAdded) {
            mLoadingDialog.dismissAllowingStateLoss()
        }
    }
}