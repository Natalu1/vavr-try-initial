package pl.hirely.user;

public class Response {
    int httpStatus;

    String response;

    public Response(int httpStatus, String response) {
        this.httpStatus = httpStatus;
        this.response = response;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getResponse() {
        return response;
    }

    public static ResponseBuilder builder() {
        return new ResponseBuilder();
    }

    static class ResponseBuilder {
        int httpStatus;
        String response;

        public ResponseBuilder status(int status) {
            this.httpStatus = status;
            return this;
        }
        public ResponseBuilder response(String response) {
            this.response = response;
            return this;
        }
        public ResponseBuilder response(Object response) {
            this.response = response.toString();
            return this;
        }

        public Response build() {
            return new Response(httpStatus, response);
        }

    }
}
