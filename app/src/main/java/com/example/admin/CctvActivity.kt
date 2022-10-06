package com.example.admin

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.system.Os.socket
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.databinding.ActivityCctvBinding
import java.io.*
import java.net.Socket
import java.net.UnknownHostException


class CctvActivity : AppCompatActivity() {
    lateinit var toggle : ActionBarDrawerToggle
    lateinit var binding: ActivityCctvBinding

    private var s: Socket? = null
    var num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCctvBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.drawer_open, R.string.drawer_close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toggle.syncState()

        binding.mainDrawerView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menuSos -> {
                    val intent = Intent(this, SosActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuCctv -> {
                    val intent = Intent(this, CctvAreaActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuArea -> {
                    val intent = Intent(this, AreaActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuCalendar -> {
                    val intent = Intent(this, ReadCalendarActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuNotification -> {
                    val intent = Intent(this, ReadNotificationActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuSalary -> {
                    val intent = Intent(this, SalaryActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        binding.logout.setOnClickListener {
            MyApplication.prefs.clear()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        Thread {
            s = Socket(SERVER_IP, 6667)

            //Log.d("메세지", "연결됨")
            //val dataInput = DataInputStream(s!!.getInputStream())
            //val msg = dataInput.readUTF()
            //Log.d("메세지", "$msg")

            Thread {
                //saveFile(s!!)
                get_fileMessage(s!!)
                //while (true) {
                    //try {
                        //val dis = DataInputStream(s!!.getInputStream())
                        //var line2 : Int = dis.read() as Int

                        //if(line2 == 10){
                            //Log.d("mobileApp", line2.toString())

                            //val dataOutput = DataOutputStream(s!!.getOutputStream())
                            //dataOutput.writeUTF("ready")

                            //get_fileMessage(s!!)
                            //break
                        //}

                //val aa = s!!.getInputStream()
                //if(aa.available() > 0){
                    //aa.bufferedReader(Charsets.UTF_8).forEachLine {
                        //Log.d("readMsg", it)
                   // }
                //}

                        //if (msg.equals("ready")) {
                            //get_fileMessage(s!!)
                        //}

                   // } catch (e: IOException) {
                      //  e.printStackTrace()
                    //} catch (e: InterruptedException) {
                      //  e.printStackTrace()
                    //}
               //}
            }.start()
        }.start()

        //s = Socket(SERVER_IP, 6667)
        //val connect: Connect = Connect()
        //connect.execute(CONNECT_MSG)


        binding.cctvBtn.setOnClickListener(View.OnClickListener {
            Thread{
                val dataOutput = DataOutputStream(s!!.getOutputStream())
                dataOutput.writeUTF("connect")
            }.start()

        })



        //val openfile = File("$filesDir/img0.png")
        //val op = BitmapFactory.Options()
        //val Bm = BitmapFactory.decodeFile(openfile.absolutePath, op)
        //binding.getImage.setImageBitmap(Bm)


    }

    private inner class Connect : AsyncTask<String?, String?, Void?>() {

        override fun doInBackground(vararg p0: String?): Void? {
            try {
                s = Socket(SERVER_IP, 6667)

                //get_fileMessage(s!!)


                //Thread{
                //while(true){
                //val `in` = BufferedReader(InputStreamReader(s!!.getInputStream()))
                //val str = `in`.readLine()
                //Log.d("mobileApp", "$str")

                //val f = File("/data/data/com.example.admin/files/img0.png")
                //val fos = FileOutputStream(f)

                //val buf = ByteArray(2048)

                //while(s!!.getInputStream().read(buf) > 0){
                //fos.write(buf)
                //fos.flush()

            //`in`.close()
            //fos.close()
            //Log.d("mobileApp", "$str" + ".png 수신완료")
            //}
            //}.start()

            //s= Socket(SERVER_IP, 6667)
            //saveFile(s!!)

            //finish() //인텐트 종료
            //overridePendingTransition(0, 0) //인텐트 효과 없애기
            //val intent = intent //인텐트
            //startActivity(intent) //액티비티 열기
            //overridePendingTransition(0, 0) //인텐트 효과 없애기

            } catch (e: UnknownHostException) {
                val str = e.message.toString()
                Log.w("discnt", "$str 1")
            } catch (e: IOException) {
                val str = e.message.toString()
                Log.w("discnt", "$str 2")
            }
            finally {
                s!!.close()
            }
            return null
        }
    }


    @Throws(IOException::class)
    private fun saveFile(clientSock: Socket) {
        val `in` = clientSock.getInputStream()
        //바이트 단위로 데이터를 읽는다, 외부로 부터 읽어들이는 역할을 담당
        //val bis = BufferedInputStream(`in`)

        val dis = DataInputStream(`in`)
        var msg = dis.readUTF()
        //val file = File("/data/data/com.example.admin/files/img0.jpg")

        //val fos = FileOutputStream(file)
        //파일을 열어서 어떤식으로 저장할지 알려준다. FileOutputStream을 쓰면 들어오는 파일과 일치하게 파일을 작성해줄 수 있는 장점이 있다.
        //var ch = bis.read()
        //while (ch != -1) {
            //Log.d("mobileApp", "dkdk")
            //fos.write(ch)
            //열린 파일시스템에 BufferedInputStream으로 외부로 부터 읽어들여온 파일을 FileOutputStream에 바로 써준다.
        //}
        Log.d("메세지", msg!!)
        //fos.close()
        `in`.close()
    }

    companion object {
        private const val SERVER_IP = "172.20.10.11"
        private const val CONNECT_MSG = "connect"
        private const val STOP_MSG = "stop"
        private const val BUF_SIZE = 100
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) return true

        return when (item.itemId) {
            R.id.action_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.home,menu)
        return true
    }

    fun get_fileMessage(clientSock: Socket) {
        try {
            val `in` = clientSock.getInputStream() // 바이트 단위로 외부로부터 데이터를 읽어 옴
            Log.d("mobileApp", "들어옴")
            val bis = BufferedInputStream(`in`)

            val file = File("/data/data/com.example.admin/files/img1.jpg")

            val buf = ByteArray(2048)

            if(file.exists()){
                file.delete()
                Log.d("mobileApp", "사진 삭제")
            }

            val fos = FileOutputStream(file) // 파일을 열어서 어떤식으로 저장할지 알려줌

            var readBytes : Int
            while (bis.read().also { readBytes = it } != -1) {
                fos.write(readBytes) // 열린 파일시스템에 BufferedInputStream으로 외부로부터 읽어 온 파일을 FileOutputStream에 바로 씀
            }

            fos.close()
            `in`.close()

            Log.d("mobileApp", "사진 수신 완료")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


