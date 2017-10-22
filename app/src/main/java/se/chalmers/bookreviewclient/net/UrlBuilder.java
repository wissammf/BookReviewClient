package se.chalmers.bookreviewclient.net;

class UrlBuilder {
    private static final String SERVER_URL = "http://185.47.129.233:3000";

    static String getPostReviewUrl() {
        return SERVER_URL + "/postReview";
    }

    static String getLoginUrl() {
        return SERVER_URL + "/login";
    }
}
