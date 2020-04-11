package com.jesulonimi.testaeppjava

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceFactory
import com.kryptokrauts.aeternity.sdk.service.aeternity.impl.AeternityService
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel
import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.math.BigInteger
import java.security.KeyPair
import java.time.LocalDateTime
import java.util.*

lateinit var aeternityService:AeternityService
private val aeternalBaseUrl="https://testnet.aeternal.io";
private val testnetBaseUrl="https://sdk-testnet.aepps.com"
lateinit var keyPairService:KeyPairService
lateinit var userKeyPair:BaseKeyPair

lateinit var privateKeyTextView:TextView
lateinit var publicKeyTextView:TextView
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.setProperty("vertx.disableFileCPResolving","true");
        setContentView(R.layout.activity_main)



        Log.d("timeonCreateStart","time is ${LocalDateTime.now()}")
        aeternityService=AeternityServiceFactory().getService(
            AeternityServiceConfiguration.configure()
                .baseUrl(testnetBaseUrl)
                .aeternalBaseUrl(aeternalBaseUrl)
                .compile()
        )
        Log.d("timeonCreateEnd","time is ${LocalDateTime.now()}")
        keyPairService=KeyPairServiceFactory().service

        Log.d("timeonCreateStartEndKs","time is ${LocalDateTime.now()}")
    }


     fun generateAeAccount(v: View){
            var generatedBaseKeyPair= keyPairService.generateBaseKeyPairFromSecret(userKeyPair.privateKey);

         Log.d("timeCreateAccount","Public Key is ${generatedBaseKeyPair.publicKey}")
         Log.d("timeCreateAccount","Private Key is ${generatedBaseKeyPair.privateKey}")
    }

    fun createAeAccount(v:View){
        Log.d("timeCreateAccountStart","time is ${LocalDateTime.now()}")
        userKeyPair= keyPairService.generateBaseKeyPair()
        Log.d("timeCreateAccountEnd","time is ${LocalDateTime.now()}")
        Log.d("timeCreateAccount","generated Public Key is ${userKeyPair.publicKey}")
        Log.d("timeCreateAccount","generated Private Key is ${userKeyPair.privateKey}")
        private_key.setText(userKeyPair.privateKey)
        public_key.setText(userKeyPair.publicKey)
    }

    fun getAeBalance(v:View){

     //   var account= aeternityService.accounts.blockingGetAccount(Optional.of(userKeyPair.publicKey))

//        Thread({
            var account= aeternityService.accounts.blockingGetAccount(Optional.of(userKeyPair.publicKey));
            Log.d("timeCreateScore","Balance is ${account.balance}")
//        }).start()

        account.balance?.let{acc_balance.setText("Balance is ${UnitConversionUtil.fromAettos(BigDecimal(account.balance), UnitConversionUtil.Unit.AE)}") }

    }

    fun sendAe(v:View){
        var pAddrToSend=public_key_edt.text.toString()
        var account = aeternityService.accounts.blockingGetAccount(Optional.of(pAddrToSend))
        var spendTx = SpendTransactionModel.builder().amount(UnitConversionUtil.toAettos("0.1", UnitConversionUtil.Unit.AE).toBigInteger()).nonce(account.nonce.add(
            BigInteger.ONE)).recipient(
           pAddrToSend).sender(userKeyPair.publicKey).ttl(BigInteger.ZERO).build()
        var result = aeternityService.transactions.blockingPostTransaction(spendTx, userKeyPair.privateKey)
        Log.d("timeCreateScore","tx-hash: ${result.txHash}")
    }
}
