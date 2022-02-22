package com.amagen.supercheap.ui.dashboard

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.amagen.supercheap.R
import com.amagen.supercheap.auth.AuthActivity
import com.amagen.supercheap.databinding.FragmentDashboardBinding
import com.amagen.supercheap.extensions.checkConnectivityStatus
import com.amagen.supercheap.extensions.hideCorners

import com.amagen.supercheap.extensions.hideKeyBoard
import com.amagen.supercheap.extensions.noInternetDialog
import com.amagen.supercheap.models.*
import com.amagen.supercheap.ui.FunctionalFragment
import com.amagen.supercheap.ui.home.recycleview.UserElementsRecycleView
import com.amagen.supercheap.ui.home.searchproducts.singleSuperSearch.SuperSearchFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_dialog.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class DashboardFragment : FunctionalFragment(), UserElementsRecycleView.OnElementClickListener {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth:FirebaseAuth
    val superElements =ArrayList<Elements>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        //creating an instance of main activity view model
        setMainActivityViewModel(requireActivity())
        mAuth= FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //--------------------if application is loading- display loading dialog-------------------//
        val loadingDialog= Dialog(requireContext())
        checkIfFragmentLoadingData(mainActivityViewModel.loadingProcessForDashboardFragment,loadingDialog)



        //--------------------get user display name-------------------//
        binding.tvUserName.text = mAuth.currentUser?.displayName




        //-------------------------init recycleview of supers-------------------------//

        val context = this
        lifecycleScope.launch(Dispatchers.IO) {

            val userFavouriteSupers = mainActivityViewModel.db.superTableOfIdAndName().getAllUserFavSupers()
            userFavouriteSupers.map {

                superElements.add(
                    Elements(
                        mainActivityViewModel.UIUserFavSuper(it.superName,it.brand),
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
                binding.rvUserSupers.layoutManager= GridLayoutManager(requireContext(),3)

            }

        }


//-------------------------observe supers from mainApp database-------------------------//


        //-------------------------observe supers from mainApp database-------------------------//
        mainActivityViewModel.listOfSupers.observe(viewLifecycleOwner){supers->
            loadingDialog.dismiss()


            //--------------Adding brand and remove numbers from the super list----------------//
            val dbSuperNames= ArrayList<String>()

            supersUIname(supers, dbSuperNames)
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_expandable_list_item_1,
                supers.map { it.superName }
            )
            binding.rvUserSupers.adapter = UserElementsRecycleView(
                superElements,
                this
            )


            binding.tvSuperFinder.threshold=1
            binding.tvSuperFinder.setAdapter(adapter)
            binding.tvSuperFinder.setOnItemClickListener { parent, view, position, id ->
                activity.hideKeyBoard()
                var superName = parent.getItemAtPosition(position)
                //----------------------add super to favorite user's super---------------------//


                binding.btnAddSuper.setOnClickListener {


                    val newSuperToAdd = supers.find {
                        it.superName == superName
                    }
                    superName = null
                    val brandToId = findBrand(newSuperToAdd!!.brand)


                    var link: String? = null
                    val listenerContext = this
                    lifecycleScope.launch(Dispatchers.IO) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            checkIfFragmentLoadingData(mainActivityViewModel.downloadAndCreateSuperTableProcess)
                        }
                        link = mainActivityViewModel.getNewLink(
                            newSuperToAdd.storeId,
                            findBrand(newSuperToAdd.brand)
                        )
                    }.invokeOnCompletion {

                        if (it != null) {
                            Toast.makeText(
                                requireContext(),
                                it.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
//                            lifecycleScope.launch(Dispatchers.Main) {
//                                checkIfFragmentLoadingData(mainActivityViewModel.downloadAndCreateSuperTableProcess)
//                            }
                            lifecycleScope.launch(Dispatchers.IO) {
                                mainActivityViewModel.createSuperItemsTable(
                                    newSuperToAdd.storeId,
                                    brandToId,
                                    link!!
                                )
                                btnAddSuper(
                                    newSuperToAdd,
                                    dbSuperNames[supers.indexOf(newSuperToAdd)]
                                )
                                superElements.add(
                                    Elements(
                                        newSuperToAdd.superName,
                                        brandToId,
                                        newSuperToAdd.storeId
                                    )
                                )

                            }.invokeOnCompletion {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    binding.rvUserSupers.adapter =
                                        UserElementsRecycleView(superElements, listenerContext)
                                }
                            }
                        }
                    }



                }
            }

        }

        //-------------------------when user type, check if list not null-------------------------//
        binding.tvSuperFinder.addTextChangedListener {
            if(mainActivityViewModel.listOfSupers.value==null){
                if(requireActivity().checkConnectivityStatus(mainActivityViewModel, lifecycleScope = requireActivity().lifecycleScope)){
                    loadingDialog.show()
                }else{
                    loadingDialog.dismiss()
                }
            }


        }
        binding.btnSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity?.startActivity(Intent(activity, AuthActivity::class.java))
            activity?.finish()
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
    }


    @SuppressLint("SetTextI18n")
    override fun onElementRootClick(elements: Elements, adapterPosition: Int) {
        dashboardViewModel.getLastUpdate(mainActivityViewModel.db,elements.storeId!!,elements.brand.brandId)
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
                StoreId_To_BrandId(elements.storeId,elements.brand.brandId),
                mainActivityViewModel,
                dialog.btn_update_dialog,
                dialog.tv_manufacturer_country.layoutDirection
            )
        }


        dialog.hideCorners()

        dialog.btn_remove.setOnClickListener {
            binding.rvUserSupers.adapter!!.notifyItemRemoved(adapterPosition)
            if(elements.storeId!=null){
                dashboardViewModel.deleteSuperTable(mainActivityViewModel.db,elements.storeId,elements.brand.brandId)
                superElements.remove(elements)
            }
            dialog.dismiss()
        }
        dialog.btn_ok.text = resources.getString(R.string.move_to_super)
        dialog.btn_ok.setOnClickListener {
            moveToSuperFragment(elements)
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onElementLongClick(elements: Elements, adapterPosition: Int) {
        moveToSuperFragment(elements)
    }

    private fun moveToSuperFragment(elements: Elements) {
        val singleSearchProduct = SuperSearchFragment(
            null,
            StoreId_To_BrandId(elements.storeId!!, elements.brand.brandId)
        )
        parentFragmentManager.beginTransaction().replace(
            R.id.nav_host_fragment_activity_main_application, singleSearchProduct
        ).addToBackStack(null).commit()
    }


}