package pl.tysia.maggwarehouse.Persistance

import android.content.Context
import android.preference.PreferenceManager
import java.net.MalformedURLException
import java.net.URL

class NetAddressManager(val context: Context) {
    companion object{
        private const val PRIVATE_NETWORK_USAGE = "private_network_usage"
        private const val TARGET_PRIVATE_NET_ADDRESS = "target_private_net_address"
        private const val TARGET_PUBLIC_NET_ADDRESS = "target_public_net_address"

        private const val PUBLIC_NET_ADDRESS = "http://martech.magg.pl/json2.aspx"
        private const val PRIVATE_NET_ADDRESS = "http://s00-pdc:7301/POST"

        fun setDefaultAddressesIfEmpty(context: Context){
            if (getConnectionURL(
                    context
                ) == null){
                PreferenceManager
                    .getDefaultSharedPreferences(context)
                    .edit()
                    .putString(
                        TARGET_PUBLIC_NET_ADDRESS,
                        PUBLIC_NET_ADDRESS
                    )
                    .apply()

                PreferenceManager
                    .getDefaultSharedPreferences(context)
                    .edit()
                    .putString(
                        TARGET_PRIVATE_NET_ADDRESS,
                        PRIVATE_NET_ADDRESS
                    )
                    .apply()
            }

        }


        private fun isPrivateNetUsed(context: Context): Boolean{
            return try {
                PreferenceManager
                    .getDefaultSharedPreferences(context)
                    .getBoolean(PRIVATE_NETWORK_USAGE, false)

            } catch (e: MalformedURLException) {
                e.printStackTrace()
                false
            }
        }

        fun getConnectionURL(context: Context): URL? {
            return try {
                val preferenceName = if (isPrivateNetUsed(
                        context
                    )
                ) TARGET_PRIVATE_NET_ADDRESS else TARGET_PUBLIC_NET_ADDRESS
                URL(
                    PreferenceManager
                        .getDefaultSharedPreferences(context)
                        .getString(preferenceName, null)
                )
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                null
            }

        }
    }



}