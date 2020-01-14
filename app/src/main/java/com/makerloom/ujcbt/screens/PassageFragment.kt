package com.makerloom.ujcbt.screens

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.franmontiel.fullscreendialog.FullScreenDialogContent
import com.franmontiel.fullscreendialog.FullScreenDialogController
import com.makerloom.ujcbt.R
import com.makerloom.ujcbt.utils.Commons

public class PassageFragment : Fragment(), FullScreenDialogContent {

    private var listener: OnFragmentInteractionListener? = null

    val TAG = PassageFragment::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_passage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val passageTV = view.findViewById<TextView>(R.id.passage_tv)
            passageTV.text = arguments?.getString(Commons.PASSAGE_KEY)
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
        else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    private var dialogController: FullScreenDialogController? = null

    override fun onDialogCreated(dialogController: FullScreenDialogController?) {
        Log.d(TAG, "onDialogCreated")
        this.dialogController = dialogController
    }

    override fun onConfirmClick(dialogController: FullScreenDialogController?): Boolean {
        Log.d(TAG, "onConfirmClick")
        val result = Bundle()
        dialogController?.confirm(result)
        return true
    }

    override fun onDiscardClick(dialogController: FullScreenDialogController?): Boolean {
        Log.d(TAG, "onDiscardClick")
        dialogController?.discard()
        return true
    }

    override fun onExtraActionClick(actionItem: MenuItem?, dialogController: FullScreenDialogController?): Boolean {
        Log.d(TAG, "onExtraActionClick")
        val result = Bundle()
        dialogController?.discardFromExtraAction(actionItem!!.itemId, result)
        return false
    }

    companion object {
        @JvmStatic
        fun newInstance(book: String?, chapter: Int, verses: ArrayList<Int>?) =
                PassageFragment().apply {
                    arguments = Bundle().apply {}
                }
    }
}
