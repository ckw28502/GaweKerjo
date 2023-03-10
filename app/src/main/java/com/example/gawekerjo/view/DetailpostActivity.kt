package com.example.gawekerjo.view

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gawekerjo.R
import com.example.gawekerjo.api.RetrofitClient
import com.example.gawekerjo.api.UserApi
import com.example.gawekerjo.database.AppDatabase
import com.example.gawekerjo.databinding.ActivityDetailpostBinding
import com.example.gawekerjo.env
import com.example.gawekerjo.model.comment.Comment
import com.example.gawekerjo.model.comment.CommentItem
import com.example.gawekerjo.model.post.Post
import com.example.gawekerjo.model.post.PostItem
import com.example.gawekerjo.model.postcomment.PostCommentItem
import com.example.gawekerjo.model.postlike.PostLikeItem
import com.example.gawekerjo.model.user.User
import com.example.gawekerjo.model.user.UserItem
import com.example.gawekerjo.repository.CommentRepository
import com.example.gawekerjo.repository.PostCommentRepository
import com.example.gawekerjo.repository.PostLikeRepository
import com.example.gawekerjo.repository.PostRepository
import com.example.gawekerjo.view.adapter.CommentAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.net.URL

class DetailpostActivity : AppCompatActivity() {
    private val coroutine = CoroutineScope(Dispatchers.IO)
    private lateinit var postRepo : PostRepository
    private lateinit var postlikeRepo : PostLikeRepository
    private lateinit var postCmntRepo : PostCommentRepository
    private lateinit var commentRepo : CommentRepository
    private lateinit var db : AppDatabase
    private lateinit var cmntAdapter : CommentAdapter
    lateinit var b : ActivityDetailpostBinding

    private var user_id = 0
    private var post_id = 0


    private var arrPost = ArrayList<PostItem>()
    private var arrPostLike = ArrayList<PostLikeItem>()
    private var arrUser = ArrayList<UserItem>()

    var tampungUser = ""

    private var arrComment = ArrayList<CommentItem>()
    private var firstFetch = true
    private lateinit var user : UserItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailpost)

        b = ActivityDetailpostBinding.inflate(layoutInflater)
        val view  = b.root
        setContentView(view)

        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        
        db = AppDatabase.Build(this)
        postRepo = PostRepository(db)
        postlikeRepo = PostLikeRepository(db)
        postCmntRepo = PostCommentRepository(db)
        commentRepo = CommentRepository(db)

        arrComment.clear()
        coroutine.launch {
            db.commentDao.clear()
        }

        try{
            user_id = intent.getIntExtra("user_id", 0)
            post_id = intent.getIntExtra("post_id", 0)
        }
        catch(e: Exception){
            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
        }

        coroutine.launch {
            var arrPost = db.postDao.getPostById(post_id) as PostItem

            b.txtTitle.setText(arrPost.title)
            b.txtKeterangan.setText(arrPost.body)


            var rc : Retrofit = RetrofitClient.getRetrofit()
            var rc_user : Call<User> = rc.create(UserApi::class.java).getUser(arrPost.user_id, null, null)

            rc_user.enqueue(object  : Callback<User>{
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    var rbody = response.body()!!
                    if(rbody.status == 200){
                        b.txtUser.text = rbody.data[0].name
                        b.txtDeskripsi.text = rbody.data[0].description
                        tampungUser = rbody.data[0].name
                        user = rbody.data[0]


                    coroutine.launch {
                        if (user.image!=null){
                            val i= URL(env.API_URL.substringBefore("/api/")+user.image).openStream()
                            val image= BitmapFactory.decodeStream(i)
                            runOnUiThread {
                                b.imgProfileFriendList.setImageBitmap(image)
                            }
                        }
                    }

                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d("CCD", "Error getting user getUser UserRepo")
                    Log.d("CCD", t.message.toString())
                    Log.d("CCD", "===============================")
                }

            })
        }


        b.btnAnswer.setText("Add Comment")
        b.btnAnswer.setOnClickListener {
            withEditText(view)
        }

        //inisialisasi imageview user
        b.imgProfileFriendList.setOnClickListener(){
            masukprofil(user)
        }

        loadComment()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    fun withEditText(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Comment")
        val dialogLayout = inflater.inflate(R.layout.layout_text_comment, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.txtComment)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialogInterface, i ->
            commentRepo.addPostComment(this, user_id, post_id, editText.text.toString())
            arrComment.add(CommentItem(10, user_id, editText.text.toString(), "", ""))
            initComment()
        }
        builder.show()
    }

    fun addCommentCallback(result: Comment){
        if (result.status == 200){
            Toast.makeText(this, "${result.message}", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, "${result.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadComment(fetched : Boolean = false){
        coroutine.launch {
            arrComment = db.commentDao.getAllComment() as ArrayList<CommentItem>
            if((arrComment.size == 0 && fetched == false) || firstFetch == true){
                firstFetch = false
                commentRepo.getAllComment(this@DetailpostActivity, post_id)
            }
            else{
                runOnUiThread {
                    initComment()
                }
            }
        }
    }

    fun initComment(){
        cmntAdapter = CommentAdapter(this, arrComment, db, tampungUser)
        b.recview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        b.recview.adapter = cmntAdapter
    }

    fun masukprofil(item: UserItem){
        if (item.type == "1"){
            var i = Intent(this, UserprofileActivity::class.java)
            i.putExtra("userLogin", item)
            i.putExtra("Action", 1)
            startActivity(i)
        }
        else{
            var i = Intent(this, CompanyProfileActivity::class.java)
            i.putExtra("userLogin", item)
            i.putExtra("Action", 1)
            startActivity(i)
        }
    }


}