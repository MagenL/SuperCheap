package com.amagen.supercheap.ui.dashboard

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amagen.supercheap.MainActivityViewModel
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.FragmentDashboardBinding
import com.amagen.supercheap.extensions.hideCorners

import com.amagen.supercheap.extensions.hideKeyBoard
import com.amagen.supercheap.models.BrandToId
import com.amagen.supercheap.models.Elements
import com.amagen.supercheap.models.IdToSuperName
import com.amagen.supercheap.models.UserFavouriteSupers
import com.amagen.supercheap.ui.FunctionalFragment
import com.amagen.supercheap.ui.home.recycleview.UserElementsRecycleView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class DashboardFragment : FunctionalFragment(), UserElementsRecycleView.OnElementClickListener {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth:FirebaseAuth
    private lateinit var superNameList:ArrayList<String>
    private lateinit var mainActivityViewModel: MainActivityViewModel

    //
    val superElements =ArrayList<Elements>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        //create instance of mainactivity viewmodel?
        mainActivityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        mAuth= FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //--------------------if application is loading- display loading dialog-------------------//
        val dialog= Dialog(requireContext())
        checkIfFragmentLoadingData(mainActivityViewModel.loadingProcessForDashboardFragment,dialog)



        //--------------------get user display name-------------------//
        binding.tvUserName.text = mAuth.currentUser?.displayName




        //-------------------------init recycleview of supers-------------------------//

        val context = this
        lifecycleScope.launch(Dispatchers.IO) {

            val userFavouriteSupers = mainActivityViewModel.db.superTableOfIdAndName().getAllUserFavSupers()
            userFavouriteSupers.map {

                superElements.add(
                    Elements(
                        UIUserFavSuper(it.superName,it.brand),
                        BrandToId.values().find { brandToId->
                            it.brand == brandToId.brandId
                        }!!,
                        it.storeId
                    ))
            }
        }.invokeOnCompletion {
            lifecycleScope.launch(Dispatchers.Main) {
                binding.rvUserSupers.adapter=
                    UserElementsRecycleView(superElements,context)
                binding.rvUserSupers.layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            }

        }




//-------------------------observe supers from mainApp database-------------------------//


        //-------------------------observe supers from mainApp database-------------------------//
        mainActivityViewModel.listOfSupers.observe(viewLifecycleOwner){supers->
            dialog.dismiss()


            //--------------Adding brand and remove numbers from the super list----------------//
            val dbSuperNames= ArrayList<String>()

            supersUIname(supers, dbSuperNames)
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_expandable_list_item_1,
                supers.map { it.superName }
            )

            binding.tvSuperFinder.threshold=1
            binding.tvSuperFinder.setAdapter(adapter)
            binding.tvSuperFinder.setOnItemClickListener { parent, view, position, id ->
                activity.hideKeyBoard()

                //----------------------add super to favorite user's super---------------------//
                binding.btnAddSuper.setOnClickListener {
                    val newSuperToAdd=supers.find {
                        it.superName==parent.getItemAtPosition(position)
                    }

                    lifecycleScope.launch(Dispatchers.IO) {
                        btnAddSuper(newSuperToAdd!!,dbSuperNames[supers.indexOf(newSuperToAdd)])
                    }
                    Log.d("superfound", newSuperToAdd.toString())

                    val brandToId=findBrand(newSuperToAdd!!.brand)

                    superElements.add(Elements(
                        newSuperToAdd!!.superName,
                        brandToId,
                        newSuperToAdd.storeId
                    ))

                    binding.rvUserSupers.adapter= UserElementsRecycleView(
                        superElements,
                        this
                    )
                    //download super //
                    mainActivityViewModel.createSuperItemsTable(newSuperToAdd.storeId,brandToId)
                    checkIfFragmentLoadingData(mainActivityViewModel.downloadAndCreateSuperTableProcess)
                }
            }

        }

        //-------------------------when user type, check if list not null-------------------------//
        binding.tvSuperFinder.addTextChangedListener {
            if(mainActivityViewModel.listOfSupers.value==null){
                dialog.show()
            }

        }
    }


    private fun btnAddSuper(superNameList: IdToSuperName, superNameToInsertToDb: String) {
        dashboardViewModel.findSuperAndSetToUserTable(
            UserFavouriteSupers(superNameList.storeId,superNameToInsertToDb,superNameList.superLink,Calendar.getInstance().timeInMillis, superNameList.brand), mainActivityViewModel.db
        )
        binding.tvSuperFinder.setText("");
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        //dashboardViewModel.deleteTableIdAndName(mainActivityViewModel.db)
    }

    @SuppressLint("SetTextI18n")
    override fun onElementRootClick(elements: Elements, adapterPosition: Int) {
        dashboardViewModel.getLastUpdate(mainActivityViewModel.db,elements.id!!,elements.brand.brandId)
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.item_dialog)
//        dialog.window!!.setLayout(300,300)
        dialog.item_name.text=elements.name
        dialog.tv_manufacturer_name.text= "company: ${elements.brand.brandName}"
        dialog.tv_unit_of_measure.visibility = View.GONE
        dialog.tv_price_to_unit.visibility = View.GONE
        dialog.tv_total_price.visibility = View.GONE
        dialog.tv_manufacturer_country.text=""

        dashboardViewModel.localDateTime.observe(viewLifecycleOwner){
            dialog.tv_manufacturer_country.text = "last update: ${it.toString().replace("T"," ")}"
        }

        lifecycleScope.launch(Dispatchers.IO) {
            checkLastSuperDbUpdate(
                mainActivityViewModel.db.superTableOfIdAndName().getFavSuperDetail(
                    elements.id,elements.brand.brandId),
                mainActivityViewModel,
                dialog.btn_update_dialog,
                dialog.tv_manufacturer_country.layoutDirection
            )
        }


        dialog.hideCorners()

        dialog.btn_remove.setOnClickListener {
            binding.rvUserSupers.adapter!!.notifyItemRemoved(adapterPosition)
            if(elements.id!=null){
                dashboardViewModel.deleteSuperTable(mainActivityViewModel.db,elements.id,elements.brand.brandId)
                superElements.remove(elements)
            }
            dialog.dismiss()
        }
        dialog.btn_ok.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onElementLongClick(elements: Elements, adapterPosition: Int) {

    }
}