package com.app.leopoly.apiHandling

import android.app.Activity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.app.leopoly.common.PrefManager
import com.app.leopoly.helper.HELPER
import com.app.leopoly.helper.HELPER.apiLog
import com.app.leopoly.helper.MySingleton
import org.json.JSONObject

object ApiRequest {
    @JvmStatic
    fun login(
        act: Activity,
        successListener: Response.Listener<String?>?,
        errorListener: Response.ErrorListener?,
        data: Map<String, String>,
        prefManager: PrefManager
    ) {
        apiLog(prefManager.ipAddress + Apis.LOGIN, "POST", data)
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                prefManager.ipAddress + Apis.LOGIN,
                successListener,
                errorListener
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    return data
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    // data.put("Authorization", "Bearer "+token);
                    return HashMap()
                }
            }
        MySingleton.getInstance(act)!!.addToRequestQueue<String>(stringRequest)
    }

    @JvmStatic
    fun getPartyList(
        act: Activity,
        successListener: Response.Listener<String?>?,
        errorListener: Response.ErrorListener?,
        prefManager: PrefManager
    ) {
        val creatingParam = prefManager.ipAddress + Apis.PARTY_LIST
        HELPER.printData("PARTY_LIST", creatingParam)
        val stringRequest: StringRequest =
            object : StringRequest(Method.GET, creatingParam, successListener, errorListener) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    return HashMap()
                }

                override fun getHeaders(): Map<String, String> {
                    return HashMap()
                }
            }
        MySingleton.getInstance(act)!!.addToRequestQueue<String>(stringRequest)
    }

    @JvmStatic
    fun getSalesPartyList(
        act: Activity,
        successListener: Response.Listener<String?>?,
        errorListener: Response.ErrorListener?,
        prefManager: PrefManager
    ) {
        val creatingParam = prefManager.ipAddress + Apis.SALES_PARTY_LIST
        HELPER.printData("SALES_PARTY_LIST", creatingParam)
        val stringRequest: StringRequest =
            object : StringRequest(Method.GET, creatingParam, successListener, errorListener) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    return HashMap()
                }

                override fun getHeaders(): Map<String, String> {
                    return HashMap()
                }
            }
        MySingleton.getInstance(act)!!.addToRequestQueue<String>(stringRequest)
    }

    @JvmStatic
    fun getAllSalesParty(
        act: Activity,
        successListener: Response.Listener<String?>?,
        errorListener: Response.ErrorListener?,
        prefManager: PrefManager
    ) {
        val creatingParam = prefManager.ipAddress + Apis.ITEM_LIST
        HELPER.printData("ITEM_LIST", creatingParam)
        val stringRequest: StringRequest =
            object : StringRequest(Method.GET, creatingParam, successListener, errorListener) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    return HashMap()
                }

                override fun getHeaders(): Map<String, String> {
                    return HashMap()
                }
            }
        MySingleton.getInstance(act)!!.addToRequestQueue<String>(stringRequest)
    }


    @JvmStatic
    fun getOrderList(
        act: Activity,
        successListener: Response.Listener<String?>?,
        errorListener: Response.ErrorListener?,
        data: Map<String, String>,
        prefManager: PrefManager
    ) {
        val creatingParam = prefManager.ipAddress + Apis.ORDER_LIST + "?party_id=" + data["partyId"]
        apiLog(creatingParam, "GET", data)
        HELPER.printData("ORDER_LIST", creatingParam)
        val stringRequest: StringRequest =
            object : StringRequest(Method.GET, creatingParam, successListener, errorListener) {
                override fun getParams(): Map<String, String> {
                    return data
                }

                override fun getHeaders(): Map<String, String> {
                    return HashMap()
                }
            }
        MySingleton.getInstance(act)!!.addToRequestQueue<String>(stringRequest)
    }

    @JvmStatic
    fun getPackageSlipList(
        act: Activity,
        successListener: Response.Listener<String?>?,
        errorListener: Response.ErrorListener?,
        data: Map<String, String>,
        prefManager: PrefManager
    ) {
        val creatingParam =
            prefManager.ipAddress + Apis.PACKAGE_SLIP_LIST + "?company_id=" + data["company_id"] + "&ac_year_id=" + data["ac_year_id"] + "&sales_ord_no=" + data["sales_ord_no"] + "&party_id=" + data["party_id"] + "&inv_item_id=" + data["inv_item_id"]
        apiLog(creatingParam, "GET", data)
        HELPER.printData("ORDER_LIST", creatingParam)
        val stringRequest: StringRequest =
            object : StringRequest(Method.GET, creatingParam, successListener, errorListener) {
                override fun getParams(): Map<String, String> {
                    return data
                }

                override fun getHeaders(): Map<String, String> {
                    return HashMap()
                }
            }
        MySingleton.getInstance(act)!!.addToRequestQueue<String>(stringRequest)
    }

    @JvmStatic
    fun addSalesOrder(
        act: Activity?,
        successListener: Response.Listener<String>,
        errorListener: Response.ErrorListener?,
        data: HashMap<String, String>,
        prefManager: PrefManager
    ) {
        apiLog(Apis.ADD_SALES_ORDER, "POST", data)
        val stringRequest: StringRequest = object :
            StringRequest(
                Method.POST,
                prefManager.ipAddress + Apis.ADD_SALES_ORDER,
                successListener,
                errorListener
            ) {
            override fun getParams(): Map<String, String> {
                return data
            }

            override fun getHeaders(): Map<String, String> {
                // data.put("Authorization", "Bearer "+token);
                return java.util.HashMap()
            }
        }
        stringRequest.tag = "addSalesOrder"
        MySingleton.getInstance(act!!)!!.addToRequestQueue(stringRequest)
    }

    @JvmStatic
    fun getPackingOrder(
        act: Activity,
        successListener: Response.Listener<String>?,
        errorListener: Response.ErrorListener?,
        data: Map<String, String>,
        prefManager: PrefManager
    ) {
        val creatingParam =
            prefManager.ipAddress + Apis.PACKING_DETAIL + "/" + data["id"]
        HELPER.print("URL:::::", creatingParam)

        apiLog(creatingParam, "GET", data)
        HELPER.print("PACKING_DETAIL", creatingParam)
        val stringRequest: StringRequest =
            object : StringRequest(Method.GET, creatingParam, successListener, errorListener) {
                override fun getParams(): Map<String, String> {
                    return data
                }

                override fun getHeaders(): Map<String, String> {
                    return HashMap()
                }
            }
        MySingleton.getInstance(act)!!.addToRequestQueue<String>(stringRequest)
    }

    @JvmStatic
    fun addPackageInfo(
        act: Activity?,
        successListener: Response.Listener<String>,
        errorListener: Response.ErrorListener?,
        data: HashMap<String, String>,
        prefManager: PrefManager
    ) {
        apiLog(prefManager.ipAddress + Apis.ADD_PACKING_DETAIL, "POST", data)
        val stringRequest: StringRequest = object :
            StringRequest(
                Method.POST,
                prefManager.ipAddress + Apis.ADD_PACKING_DETAIL,
                successListener,
                errorListener
            ) {
            override fun getParams(): Map<String, String> {
                return data
            }

            override fun getHeaders(): Map<String, String> {
                // data.put("Authorization", "Bearer "+token);
                return java.util.HashMap()
            }
        }
        stringRequest.tag = "addPackage"
        MySingleton.getInstance(act!!)!!.addToRequestQueue(stringRequest)
    }

    @JvmStatic
    fun getPackageDetailList(
        act: Activity,
        successListener: Response.Listener<String>?,
        errorListener: Response.ErrorListener?,
        data: Map<String, String>,
        prefManager: PrefManager
    ) {
        val creatingParam =
            prefManager.ipAddress + Apis.PACKAGE_DETAIL_LIST + "/" + data["order_id"]
        apiLog(creatingParam, "GET", data)
        HELPER.printData("getPackageDetailList", creatingParam)
        val stringRequest: StringRequest =
            object : StringRequest(Method.GET, creatingParam, successListener, errorListener) {
                override fun getParams(): Map<String, String> {
                    return data
                }

                override fun getHeaders(): Map<String, String> {
                    return HashMap()
                }
            }
        MySingleton.getInstance(act)!!.addToRequestQueue<String>(stringRequest)
    }

    @JvmStatic
    fun deletePackageById(
        act: Activity,
        successListener: Response.Listener<String>?,
        errorListener: Response.ErrorListener?,
        data: Map<String, String>,
        prefManager: PrefManager
    ) {
        val creatingParam =
            prefManager.ipAddress + Apis.DELETE_PACKAGE_BY_ID + "/" + data["package_id"]
        apiLog(creatingParam, "DELETE", data)
        HELPER.printData("deletePackageById", creatingParam)
        val stringRequest: StringRequest =
            object : StringRequest(Method.DELETE, creatingParam, successListener, errorListener) {
                override fun getParams(): Map<String, String> {
                    return data
                }

                override fun getHeaders(): Map<String, String> {
                    return HashMap()
                }
            }
        MySingleton.getInstance(act)!!.addToRequestQueue<String>(stringRequest)
    }

    @JvmStatic
    fun deleteOrderById(
        act: Activity,
        successListener: Response.Listener<String>?,
        errorListener: Response.ErrorListener?,
        data: Map<String, String>,
        prefManager: PrefManager
    ) {
        val creatingParam =
            prefManager.ipAddress + Apis.DELETE_ORDER_BY_ID + "/" + data["order_id"]
        apiLog(creatingParam, "DELETE", data)
        HELPER.printData("deleteOrderById", creatingParam)
        val stringRequest: StringRequest =
            object : StringRequest(Method.DELETE, creatingParam, successListener, errorListener) {
                override fun getParams(): Map<String, String> {
                    return data
                }

                override fun getHeaders(): Map<String, String> {
                    return HashMap()
                }
            }
        MySingleton.getInstance(act)!!.addToRequestQueue<String>(stringRequest)
    }

    @JvmStatic
    fun addSalesOrderDetail(
        act: Activity?,
        successListener: Response.Listener<JSONObject>,
        errorListener: Response.ErrorListener?,
        data: JSONObject,
        prefManager: PrefManager
    ) {
        HELPER.print(Apis.ADD_SALES_ORDER_DETAIL, data.toString())
        val stringRequest: JsonObjectRequest = object :
            JsonObjectRequest(
                Method.POST,
                prefManager.ipAddress + Apis.ADD_SALES_ORDER_DETAIL,
                data,
                successListener,
                errorListener
            ) {
            override fun getParams(): MutableMap<String, String>? {
                return super.getParams()
            }

            override fun getHeaders(): Map<String, String> {
                // data.put("Authorization", "Bearer "+token);
                return HashMap()
            }
        }
        MySingleton.getInstance(act!!)!!.addToRequestQueue(stringRequest)
    }

    @JvmStatic
    fun getTransportList(
        act: Activity,
        successListener: Response.Listener<String?>?,
        errorListener: Response.ErrorListener?,
        prefManager: PrefManager
    ) {
        val creatingParam = prefManager.ipAddress + Apis.TRANSPORT_LIST
        HELPER.printData("PARTY_LIST", creatingParam)
        val stringRequest: StringRequest =
            object : StringRequest(Method.GET, creatingParam, successListener, errorListener) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    return HashMap()
                }

                override fun getHeaders(): Map<String, String> {
                    return HashMap()
                }
            }
        MySingleton.getInstance(act)!!.addToRequestQueue<String>(stringRequest)
    }

    @JvmStatic
    fun getSalesOrderNo(
        act: Activity,
        successListener: Response.Listener<String?>?,
        errorListener: Response.ErrorListener?,
        data: HashMap<String, String>,
        prefManager: PrefManager
    ) {
        val creatingParam = prefManager.ipAddress + Apis.GET_SALES_ORDER_NO
        HELPER.printData("SALES_ORDER_NO_LIST", creatingParam)
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, creatingParam, successListener, errorListener) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    return data
                }

                override fun getHeaders(): Map<String, String> {
                    return HashMap()
                }
            }
        MySingleton.getInstance(act)!!.addToRequestQueue(stringRequest)
    }

    @JvmStatic
    fun getLessWt(
        act: Activity,
        successListener: Response.Listener<String?>?,
        errorListener: Response.ErrorListener?,
        data: HashMap<String, String>,
        prefManager: PrefManager
    ) {
        val creatingParam = prefManager.ipAddress + Apis.GET_LESS_WT
        HELPER.printData("LESS_WT", creatingParam)
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, creatingParam, successListener, errorListener) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    return data
                }

                override fun getHeaders(): Map<String, String> {
                    return HashMap()
                }
            }
        MySingleton.getInstance(act)!!.addToRequestQueue(stringRequest)
    }

    @JvmStatic
    fun getPackageSlipDetailList(
        act: Activity,
        successListener: Response.Listener<String>?,
        errorListener: Response.ErrorListener?,
        data: Map<String, String>,
        prefManager: PrefManager
    ) {

        val creatingParam = prefManager.ipAddress + Apis.PACKAGE_SLIP_DETAIL_LIST + "?company_id=" + data["company_id"]+ "&ac_year_id=" + data["ac_year_id"]+ "&inv_date=" + data["inv_date"]
//        val creatingParam =
//            prefManager.ipAddress + Apis.PACKAGE_SLIP_DETAIL_LIST
        apiLog(creatingParam, "GET", data)
        HELPER.printData("getPackageSlipDetailList", creatingParam)
        val stringRequest: StringRequest =
            object : StringRequest(Method.GET, creatingParam, successListener, errorListener) {
                override fun getParams(): Map<String, String> {
                    return data
                }

                override fun getHeaders(): Map<String, String> {
                    return HashMap()
                }
            }
        MySingleton.getInstance(act)!!.addToRequestQueue<String>(stringRequest)
    }

}