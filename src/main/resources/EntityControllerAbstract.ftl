package ${packageName};

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.xnou.util.data.WrappedResponse;

/**
 * 自动生成的WEB控制器抽象基类，提供通用的参数和方法。
 * 
 * @author ${author!"OU Xingning"}
 * @date ${date?string("yyyy/MM/dd")}
 */
public abstract class ${classShortName} {

    /**
     * 分页查询时每页显示的记录数。
     */
    protected static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 列表查询时返回的最多记录数。
     */
    protected static final int DEFAULT_LIST_LIMIT = 100;

    /**
     * 处理成功的HTTP响应内容，响应200状态码。
     * 
     * @param data
     *            响应内容
     */
    protected ResponseEntity<WrappedResponse> success(Object data) {
        return this.success(200, data, HttpStatus.OK);
    }

    /**
     * 处理成功的HTTP响应内容。
     * 
     * @param code
     *            响应代码，非HTTP状态码
     * @param data
     *            响应内容
     * @param httpStatus
     *            HTTP的响应状态码
     */
    protected ResponseEntity<WrappedResponse> success(int code, Object data, HttpStatus httpStatus) {
        MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        WrappedResponse resp = WrappedResponse.success(code, data);
        return new ResponseEntity<WrappedResponse>(resp, headers, httpStatus);
    }

    /**
     * 处理失败，抛出异常，注意：响应代码为500，响应HTTP状态码为200
     * 
     * @param message
     *            失败代码或提示
     */
    protected ResponseEntity<WrappedResponse> fail(String message) {
        return this.fail(message, "");
    }

    /**
     * 处理失败，抛出异常，注意：响应代码为500，响应HTTP状态码为200
     * 
     * @param code
     *            错误码
     * @param message
     *            失败代码或提示
     */
    protected ResponseEntity<WrappedResponse> fail(int code, String message) {
        return this.fail(code, message, "", HttpStatus.OK);
    }

    /**
     * 处理失败，抛出异常，注意：响应代码为500，响应HTTP状态码为200
     * 
     * @param message
     *            失败代码或提示
     * @param cause
     *            导致失败的异常或原因
     */
    protected ResponseEntity<WrappedResponse> fail(String message, Object cause) {
        return this.fail(500, message, cause, HttpStatus.OK);
    }

    /**
     * 处理失败，抛出异常。
     * 
     * @param code
     *            响应代码，非HTTP状态码
     * @param message
     *            失败代码或提示
     * @param cause
     *            导致失败的异常或原因
     * @param httpStatus
     *            HTTP的响应状态码
     */
    protected ResponseEntity<WrappedResponse> fail(int code, String message, Object cause, HttpStatus httpStatus) {
        MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        WrappedResponse resp = WrappedResponse.fail(code, message, cause);
        return new ResponseEntity<WrappedResponse>(resp, headers, httpStatus);
    }

    /**
     * 发生错误，由用户提交的参数非法而引起，注意：响应代码为400，响应HTTP状态码为200
     * 
     * @param message
     *            错误代码或提示
     */
    protected ResponseEntity<WrappedResponse> error(String message) {
        return this.error(message, "");
    }

    /**
     * 发生错误，由用户提交的参数非法而引起，注意：响应代码为400，响应HTTP状态码为200
     * 
     * @param code
     *            响应代码，非HTTP状态码
     * @param message
     *            错误代码或提示
     */
    protected ResponseEntity<WrappedResponse> error(int code, String message) {
        return this.error(code, message, "", HttpStatus.OK);
    }

    /**
     * 发生错误，由用户提交的参数非法而引起，注意：响应代码为500，响应HTTP状态码为200
     * 
     * @param message
     *            错误代码或提示
     * @param cause
     *            错误原因
     */
    protected ResponseEntity<WrappedResponse> error(String message, Object cause) {
        return this.error(400, message, cause, HttpStatus.OK);
    }

    /**
     * 发生错误，由用户提交的参数非法而引起。
     * 
     * @param code
     *            响应代码，非HTTP状态码
     * @param message
     *            错误代码或提示
     * @param cause
     *            错误原因
     * @param httpStatus
     *            HTTP的响应状态码
     */
    protected ResponseEntity<WrappedResponse> error(int code, String message, Object cause, HttpStatus httpStatus) {
        MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        WrappedResponse resp = WrappedResponse.error(code, message, cause);
        return new ResponseEntity<WrappedResponse>(resp, headers, httpStatus);
    }
}
