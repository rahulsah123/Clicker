package com.example.retrofit


import retrofit2.adapter.rxjava.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

public class RetrofitError {
    companion object {
        fun codeToErrorMessage(code: Int): String {
            return if (code >= 400 && code < 500)
                "serverIssues"
            else if (code >= 400 && code < 500)
                "serverIssues"
            else
                "unResolveErrorr"
        }

        fun showErrorMessage(error: Throwable): String {
            if (error is SocketTimeoutException) {
                return "No Internet Connection"
            } else if (error is UnknownHostException) {
                return "serverIssues"
            } else if (error is HttpException) {
                val code = (error as HttpException).code()
                return codeToErrorMessage(code)
            } else {
                return "unResolveErrorr"
            }
        }
    }
}