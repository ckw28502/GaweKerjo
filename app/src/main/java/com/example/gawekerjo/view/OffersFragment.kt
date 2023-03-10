package com.example.gawekerjo.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gawekerjo.R
import com.example.gawekerjo.database.AppDatabase
import com.example.gawekerjo.databinding.FragmentOffersBinding
import com.example.gawekerjo.env
import com.example.gawekerjo.model.Offer.OfferItem
import com.example.gawekerjo.model.chat.ChatItem
import com.example.gawekerjo.model.user.UserItem
import com.example.gawekerjo.model.userchat.UserChatItem
import com.example.gawekerjo.repository.ChatRepository
import com.example.gawekerjo.repository.OfferRepository
import com.example.gawekerjo.view.adapter.RVAdapterJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

class OffersFragment(var mc : HomeActivity, var db : AppDatabase, var user : UserItem) : Fragment() {

    private lateinit var adapterJob : RVAdapterJob
    private lateinit var b : FragmentOffersBinding
    private val coroutine = CoroutineScope(Dispatchers.IO)
    private lateinit var ctx : Context
    private var listOffer : List<OfferItem> = listOf()
    private lateinit var chatRepo:ChatRepository

    private lateinit var offerRepo : OfferRepository

    private var firstFetch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ctx = view.context
        offerRepo = OfferRepository(db)
        chatRepo= ChatRepository(db)
        adapterJob = RVAdapterJob(this, R.layout.layout_rv_offer, listOffer, db)

        b.btnOfferSearch.setOnClickListener {
            if(b.loadModal.visibility == View.VISIBLE){
                return@setOnClickListener
            }

            listOffer = listOf()
            initRV()

            loadData(title = b.txtSearchJob.text.toString())
            b.loadModal.visibility = View.VISIBLE
        }

        b.loadModal.visibility = View.VISIBLE
        loadData()
    }

    fun dialog(offer : OfferItem, useroffer : UserItem) {
        val b=layoutInflater.inflate(R.layout.dialog_layout_detailoffer,null)
        val d=Dialog(mc)
        with(d) {
            setContentView(b)
            setCancelable(true)
            val btnClose = b.findViewById<ImageButton>(R.id.btnOfferDetailClose)

            val txtTitle = b.findViewById<TextView>(R.id.txtOfferDetailTitle)
            val txtSkill = b.findViewById<TextView>(R.id.txtOfferDetailSkill)
            val txtBody = b.findViewById<TextView>(R.id.txtOfferDetailBody)
            val txtUser = b.findViewById<TextView>(R.id.txtOfferDetailUser)
            val txtLocation = b.findViewById<TextView>(R.id.txtOfferDetailUserLocation)

            val txtDivider = b.findViewById<TextView>(R.id.txtDivider)

            val btnApply = b.findViewById<Button>(R.id.btnOfferDetailApply)
            val btnEdit = b.findViewById<Button>(R.id.btnofferDetailEdit)
            val btnDelete = b.findViewById<Button>(R.id.btnOfferDetailDelete)

            val imgOfferDialog = b.findViewById<ImageView>(R.id.imgOfferDialog)

            btnEdit.visibility = View.GONE
            btnDelete.visibility = View.GONE

            if(user.id == useroffer.id){
                btnApply.visibility = View.GONE
            }

            txtDivider.visibility = View.INVISIBLE

            txtTitle.text = offer.title
            txtSkill.text = offer.skills
            txtBody.text = offer.body
            txtUser.text = useroffer.name
            txtLocation.text = useroffer.lokasi
            if(useroffer.lokasi != null){
                if(useroffer.lokasi!!.lowercase() == "choose"){
                    txtLocation.text = "-"
                }
            }

            coroutine.launch {
                if (useroffer.image!=null){
                    val i= URL(env.API_URL.substringBefore("/api/")+useroffer.image).openStream()
                    val image= BitmapFactory.decodeStream(i)
                     mc.runOnUiThread{
                        imgOfferDialog.setImageBitmap(image)
                    }
                }
            }

            if(useroffer.id == user.id){
                btnApply.visibility = View.GONE
            }

            btnClose.setOnClickListener {
                this.dismiss()
            }

            btnApply.setOnClickListener {
                // CHAT KE ORANG E
                chatRepo.offertoChat(this@OffersFragment,user.id,useroffer)

            }

            imgOfferDialog.setOnClickListener {
                // KE HALAMAN ORANG E. pake variabel user

                if (useroffer.type == "1"){
                    var i = Intent(requireContext(), UserprofileActivity::class.java)
                    i.putExtra("userLogin", useroffer)
                    i.putExtra("Action", 1)
                    startActivity(i)
                }
                else{
                    var i = Intent(requireContext(), CompanyProfileActivity::class.java)
                    i.putExtra("userLogin", useroffer)
                    i.putExtra("Action", 1)
                    startActivity(i)
                }

            }

            show()
        }
    }

    fun loadData(fetched : Boolean = false, title : String? = null){
        coroutine.launch {
            listOffer = db.offerDao.fetch()
            if((listOffer.size == 0 && fetched == false) || title != null || firstFetch == true){
                firstFetch = false
                offerRepo.searchOffer(this@OffersFragment,title)
            }else{
                mc.runOnUiThread {
                    b.loadModal.visibility = View.GONE
                    initRV()
                }
            }
        }
    }

    fun initRV(){
        adapterJob = RVAdapterJob(this, R.layout.layout_rv_offer, listOffer, db)
        b.rvOffer.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        b.rvOffer.adapter = adapterJob
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentOffersBinding.inflate(inflater, container, false)
        return b.root
    }

    fun gotoChat(hchat: ChatItem, chat: List<UserChatItem>, recipient: UserItem) {
        val i=Intent(this.context,DetailChatActivity::class.java)
        i.putExtra("user",user)
        i.putExtra("rec",recipient)
        i.putExtra("hchat",hchat)
        i.putParcelableArrayListExtra("chat",chat as ArrayList)
        Log.d("pindah","bisa")
        startActivity(i)
    }

}