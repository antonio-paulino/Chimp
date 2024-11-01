package pt.isel.pdm.chimp.infrastructure.services.http

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import pt.isel.pdm.chimp.ChimpApplication.Companion.TAG
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem

/**
 *
 * Represents the base class for the HTTP service
 * that communicates with the backend.
 *
 * @property httpClient the HTTP client
 * @property baseUrl the base URL of the API
 */
open class BaseHTTPService(
    protected val httpClient: HttpClient,
    protected val baseUrl: String,
) {
    /**
     * Parses the response of an API request.
     *
     * @return the result of the request
     */
    protected suspend inline fun <reified R> HttpResponse.parseResponse(): ApiResult<R?, Problem> {
        return try {
            return when (status) {
                HttpStatusCode.OK -> {
                    success(body<R>())
                }

                HttpStatusCode.Created -> {
                    success(body<R>())
                }

                HttpStatusCode.NoContent -> {
                    success(null)
                }

                HttpStatusCode.BadRequest -> {
                    failure(body<Problem.InputValidationProblem>())
                }

                HttpStatusCode.InternalServerError -> {
                    error()
                }

                else -> {
                    failure(body<Problem.ServiceProblem>())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting response", e)
            failure(Problem.UnexpectedProblem)
        }
    }

    /**
     * Performs a GET request to the specified resource.
     *
     * @param resource the resource to get
     * @param token the token to use in the request
     *
     * @return the result of the request
     */
    protected suspend inline fun <reified R> get(
        resource: String,
        token: String,
    ): ApiResult<R?, Problem> =
        httpClient.get {
            url("$baseUrl/$resource")
            header(AUTHORIZATION_HEADER, "Bearer $token")
        }.parseResponse()

    /**
     * Performs a PUT request to the specified resource.
     *
     * @param resource the resource to put
     * @param token the token to use in the request
     * @param body the body of the request
     *
     * [T] the type of the body of the request
     * [R] the type of the response
     *
     * @return the result of the request
     */
    protected suspend inline fun <reified T, reified R> put(
        resource: String,
        token: String,
        body: T?,
    ): ApiResult<R?, Problem> =
        httpClient.put {
            url("$baseUrl/$resource")
            header(AUTHORIZATION_HEADER, "Bearer $token")
            body?.let {
                header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                setBody(it)
            }
        }.parseResponse()

    /**
     * Performs a POST request to the specified resource.
     *
     * [T] the type of the body of the request
     * [R] the type of the response
     *
     * @param resource the resource to post
     * @param token the token to use in the request
     * @param body the body of the request
     *
     * @return the result of the request
     */
    protected suspend inline fun <reified T, reified R> post(
        resource: String,
        token: String,
        body: T?,
    ): ApiResult<R?, Problem> =
        httpClient.post {
            url("$baseUrl/$resource")
            header(AUTHORIZATION_HEADER, "Bearer $token")
            body?.let {
                header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                setBody(it)
            }
        }.parseResponse()

    /**
     * Performs a DELETE request to the specified resource.
     *
     * @param resource the resource to delete
     * @param token the token to use in the request
     *
     * @return the result of the request
     */
    protected suspend inline fun delete(
        resource: String,
        token: String,
    ): ApiResult<Unit?, Problem> =
        httpClient.delete {
            url("$baseUrl/$resource")
            header(AUTHORIZATION_HEADER, "Bearer $token")
        }.parseResponse()

    /**
     * Performs a PATCH request to the specified resource.
     *
     * [T] the type of the body of the request
     * [R] the type of the response
     *
     * @param resource the resource to patch
     * @param token the token to use in the request
     * @param body the body of the request
     * @return the result of the request
     */
    protected suspend inline fun <reified T, reified R> patch(
        resource: String,
        token: String,
        body: T?,
    ): ApiResult<R?, Problem> =
        httpClient.patch {
            url("$baseUrl/$resource")
            header(AUTHORIZATION_HEADER, "Bearer $token")
            body?.let {
                header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                setBody(it)
            }
        }.parseResponse()

    companion object {
        protected const val AUTHORIZATION_HEADER = "Authorization"
        protected const val CONTENT_TYPE_HEADER = "Content-Type"
        protected const val APPLICATION_JSON = "application/json"
    }
}
