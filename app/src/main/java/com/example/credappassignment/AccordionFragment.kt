package com.example.credappassignment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.accordion_fragment.*


class AccordionFragment : Fragment(), View.OnClickListener {
    private var expandableLayout0: ExpandableLayout? = null
    private var expandableLayout1: ExpandableLayout? = null
    private var progr = 0
    private var expandableLayout2: ExpandableLayout? = null
    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.accordion_fragment, container, false)
        expandableLayout0 = rootView.findViewById(R.id.expandable_layout_0)
        expandableLayout1 = rootView.findViewById(R.id.expandable_layout_1)
        expandableLayout2 = rootView.findViewById(R.id.expandable_layout_2)

        expandableLayout0?.setOnExpansionUpdateListener(object :
            ExpandableLayout.OnExpansionUpdateListener {
            override fun onExpansionUpdate(expansionFraction: Float, state: Int) {
                Log.d("ExpandableLayout0", "State: $state")
            }
        })

        expandableLayout1?.setOnExpansionUpdateListener(object :
            ExpandableLayout.OnExpansionUpdateListener {
            override fun onExpansionUpdate(expansionFraction: Float, state: Int) {
                Log.d("ExpandableLayout1", "State: $state")
            }
        })

        expandableLayout2?.setOnExpansionUpdateListener(object :
            ExpandableLayout.OnExpansionUpdateListener {
            override fun onExpansionUpdate(expansionFraction: Float, state: Int) {
                Log.d("ExpandableLayout1", "State: $state")
            }
        })



        rootView.findViewById<View>(R.id.expand_button_0).setOnClickListener(this)
        rootView.findViewById<View>(R.id.proceed_button).setOnClickListener(this)
        rootView.findViewById<View>(R.id.select_bank_button).setOnClickListener(this)
        rootView.findViewById<View>(R.id.expand_button_2).setOnClickListener(this)
        rootView.findViewById<View>(R.id.select_bank_button2).setOnClickListener(this)

rootView.findViewById<Button>(R.id.button_incr).setOnClickListener {
        if (progr <= 900) {
            progr += 100
            updateProgressBar()
        }

}
        rootView.findViewById<Button>(R.id.button_decr).setOnClickListener {
            if (progr >= 100) {
                progr -= 100
                updateProgressBar()
            }
        }
        //rootView.findViewById<View>(R.id.expand_button_1).setOnClickListener(this)
        return rootView


    }

    private fun updateProgressBar() {
        progress_bar.progress = progr / 10
        text_view_progress.text = "₹ " + progr
    }


    override fun onClick(view: View) {

        if (view.id == R.id.proceed_button) {
            if (progr!=0){
                text_view222.text = "₹ " + progr
                textView.text = "₹ " + progr +" /mo"
                textView22222.text = "₹ " + progr/2 + " /mo"
                expandableLayout0?.collapse()
                expandableLayout1?.expand()
            }
            else{
                Toast.makeText(getActivity(), "Please select valid amount",
                    Toast.LENGTH_LONG).show();
            }

        }
        if (view.id ==R.id.expand_button_0){
            expandableLayout0?.expand()
            expandableLayout1?.collapse()
        }

            if (view.id ==R.id.select_bank_button){

                if (check_box.isChecked || check_box2.isChecked){
                if (check_box.isChecked){
                    check_box2.isChecked =false
                    text_view222_3.text = "1 month"
                    text_view2223.text ="₹ " + progr +" /mo"
                }
                if (check_box2.isChecked){
                    check_box.isChecked =false
                    text_view222_3.text = "2 months"
                    text_view2223.text = "₹ " + progr/2 + " /mo"
                }


                expandableLayout2?.expand()
                expandableLayout1?.collapse()
                expandableLayout0?.collapse()
            }
                else{
                    Toast.makeText(getActivity(), "Please select one of the plan",
                        Toast.LENGTH_LONG).show();
                }
        }

        if (view.id==R.id.expand_button_2){
            expandableLayout0?.collapse()
            expandableLayout2?.collapse()
            expandableLayout1?.expand()
        }
        if (view.id==R.id.select_bank_button2){
            if (check_box3.isChecked){
                Toast.makeText(getActivity(), "Submitted successfully",
                    Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getActivity(), "Please select bank account",
                    Toast.LENGTH_LONG).show();
            }
        }

    }



}