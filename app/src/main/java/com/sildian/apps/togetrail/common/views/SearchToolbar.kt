package com.sildian.apps.togetrail.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.databinding.ViewToolbarSearchBinding

/*************************************************************************************************
 * A custom toolbar including a search text field
 ************************************************************************************************/

class SearchToolbar(context: Context, attrs: AttributeSet): AppBarLayout(context, attrs) {

    /********************************UI components***********************************************/

    private lateinit var binding: ViewToolbarSearchBinding
    val toolbar: MaterialToolbar get() = this.binding.viewToolbarSearchToolbar

    /*******************************Xml attributes***********************************************/

    var researchTextHint = ""
        set(value) {
            field = value
            this.binding.viewToolbarSearchTextFieldResearch.hint = field
        }

    /**********************************Callbacks*************************************************/

    var onResearchFocusChange: ((v: View, hasFocus: Boolean) -> Unit)? = null
    var onResearchUpdated: ((v: View, research: String) -> Unit)? = null
    var onResearchCleared: ((v: View) -> Unit)? = null

    /*********************************UI Monitoring**********************************************/

    init {
        initView()
        initAttributes(attrs)
        initResearchTextField()
        initClearResearchButton()
    }

    private fun initView() {
        this.binding = ViewToolbarSearchBinding.inflate(LayoutInflater.from(context))
        addView(this.binding.root)
    }

    private fun initAttributes(attrs: AttributeSet) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.SearchToolbar, 0, 0).apply {
            researchTextHint = getString(R.styleable.SearchToolbar_researchTextHint)?: ""
        }
    }

    private fun initResearchTextField() {
        this.binding.viewToolbarSearchTextFieldResearch.doOnTextChanged { text, start, before, count ->
            updateClearResearchButtonVisibility()
            this.onResearchUpdated?.invoke(this, text?.toString()?: "")
        }
        this.binding.viewToolbarSearchTextFieldResearch.setOnFocusChangeListener { v, hasFocus ->
            this.onResearchFocusChange?.invoke(this, hasFocus)
        }
    }

    private fun initClearResearchButton() {
        this.binding.viewToolbarSearchButtonClearResearch.setOnClickListener {
            this.binding.viewToolbarSearchTextFieldResearch.text.clear()
            this.onResearchCleared?.invoke(this)
        }
    }

    private fun updateClearResearchButtonVisibility() {
        val shouldShowClearResearchButton = !this.binding.viewToolbarSearchTextFieldResearch.text.isNullOrEmpty()
        this.binding.viewToolbarSearchButtonClearResearch.isVisible = shouldShowClearResearchButton
    }

    fun requestResearchFocus() {
        this.binding.viewToolbarSearchTextFieldResearch.requestFocus()
    }

    fun clearResearchFocus() {
        this.binding.viewToolbarSearchTextFieldResearch.clearFocus()
    }

    fun setCurrentResearch(research: String) {
        this.binding.viewToolbarSearchTextFieldResearch.setText(research)
    }
}