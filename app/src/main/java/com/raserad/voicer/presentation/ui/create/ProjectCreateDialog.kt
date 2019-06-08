package com.raserad.voicer.presentation.ui.create

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import com.arellomobile.mvp.MvpAppCompatDialogFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.raserad.voicer.R
import com.raserad.voicer.di.AppDI
import com.raserad.voicer.presentation.mvp.create.ProjectCreatePresenter
import com.raserad.voicer.presentation.mvp.create.ProjectCreateView
import kotlinx.android.synthetic.main.dialog_project_create.view.*

class ProjectCreateDialog: MvpAppCompatDialogFragment(), ProjectCreateView {

    @InjectPresenter
    lateinit var presenter: ProjectCreatePresenter

    private lateinit var dialogView: View

    @ProvidePresenter
    fun providePresenter() = AppDI.getPresenterDI()
        .getProjectCreate(AppDI.getRouter())

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)

        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_project_create, null, false)

        configure(dialogView)

        builder.setView(dialogView)

        return builder.create()
    }

    private fun configure(view: View) {
        view.titleView.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.rememberTitle(view.titleView.text.toString())
            }

        })

        view.descriptionView.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.rememberDescription(view.descriptionView.text.toString())
            }

        })

        view.createButton.setOnClickListener {
            presenter.create()
        }

        view.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun showCreateProgress(isShow: Boolean) {
        dialogView.createProgress.visibility = if(isShow) View.VISIBLE else View.GONE
        dialogView.content.visibility = if(isShow) View.GONE else View.VISIBLE
    }

    override fun showKeyboard() {
        dialogView.titleView.requestFocus()
    }

    override fun showTitleEmptyError(isShow: Boolean) {
        dialogView.titleHintView.error = if(isShow) "Обязательное поле" else ""
    }
}