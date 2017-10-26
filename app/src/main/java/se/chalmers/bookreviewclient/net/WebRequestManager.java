package se.chalmers.bookreviewclient.net;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import se.chalmers.bookreviewclient.model.Errors;

public class WebRequestManager {
    private static WebRequestManager instance;

    private RequestQueue requestQueue;

    private WebRequestManager() {

    }

    public static WebRequestManager getInstance() {
        if (instance == null) {
            instance = new WebRequestManager();
        }

        return instance;
    }

    public void initialize(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void postBookReview(final int bookId, final float rating, final String text, final String token, final WebRequestHandler requestHandler) {
        StringRequest request = new StringRequest(Request.Method.POST, UrlBuilder.getPostReviewUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (requestHandler != null) {
                            requestHandler.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (requestHandler != null) {
                            requestHandler.onFailure(Errors.ConnectionError);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("book", String.valueOf(bookId));
                params.put("rating", String.valueOf(rating));
                params.put("review", text);
                params.put("language", "1");

                return params;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Authorization", "JWT "+ token);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(3000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    public void login(final String username, final String password, final WebRequestHandler requestHandler) {
        StringRequest request = new StringRequest(Request.Method.POST, UrlBuilder.getLoginUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (requestHandler != null) {
                            try {
                                JSONObject responseObject = new JSONObject(response);

                                String error = responseObject.getString("error");
                                switch (error) {
                                    case "0":
                                        String token = responseObject.getString("jwt");
                                        requestHandler.onSuccess(token);
                                        break;
                                    case "1":
                                        requestHandler.onFailure(Errors.UsernamePasswordIncorrectError);
                                        break;
                                    case "2":
                                        requestHandler.onFailure(Errors.ServerError);
                                        break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (requestHandler != null) {
                            requestHandler.onFailure(Errors.ConnectionError);
                        }
                    }
                }) {

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                String encodedUsername;
                String encodedPassword;
                try {
                    encodedUsername = URLEncoder.encode(username, "utf-8");
                    encodedPassword = URLEncoder.encode(password, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    encodedUsername = "";
                    encodedPassword = "";
                }

                params.put("username", encodedUsername);
                params.put("password", encodedPassword);

                return params;
            }
        };
        requestQueue.add(request);
    }

    public interface WebRequestHandler {
        void onSuccess(Object data);

        void onFailure(Errors error);
    }
}
