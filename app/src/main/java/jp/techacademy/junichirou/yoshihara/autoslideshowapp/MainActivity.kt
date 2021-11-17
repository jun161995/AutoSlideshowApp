package jp.techacademy.junichirou.yoshihara.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.net.Uri
import android.os.Handler
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    var list = ArrayList<Uri>()// 配列はグローバルにしたい,String型ではなくUri型
    var index = 0
    private var mTimer: Timer? = null

    // タイマー用の時間のための変数
    private var mTimerSec = 0.0

    private var mHandler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        back_button.setOnClickListener {
            if (index > 0) {
                index = index - 1
                imageView.setImageURI(list[index])
            } else {
                index = list.size - 1
                imageView.setImageURI(list[index])
            }
        }
        pause_button.setOnClickListener {
            if (mTimer == null) {
                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        if (index == list.size - 1) { // =は代入になってしまうため、条件式は==で表す
                            index = 0
                            imageView.setImageURI(list[index])
                        } else {
                            index = index + 1
                            imageView.setImageURI(list[index])
                        }
                    }
                }, 2000, 2000)// 最初に始動させるまで2秒、ループの間隔を2秒 に設定
            }
        }
        foward_button.setOnClickListener {
            if (index == list.size - 1) { // =は代入になってしまうため、条件式は==で表す
                index = 0
                imageView.setImageURI(list[index])
            } else {
                index = index + 1
                imageView.setImageURI(list[index])
            }
        }


        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }


    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver

        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )
        if (cursor!!.moveToFirst()) {

            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                list.add(imageUri)

                Log.d("ANDROID", "URI : " + imageUri.toString())
            } while (cursor.moveToNext())
            imageView.setImageURI(list[index])
        }
        cursor.close()


    }
}