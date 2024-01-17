import android.content.Context
import okhttp3.Interceptor
import okhttp3.Request

class JwtTokenManager(private val context: Context) {
    // 엑세스 토큰 저장
    fun saveAccessToken(token: String) {
        val sharedPreferences = context.getSharedPreferences("recipia", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("access_token", token)
        editor.apply()
    }

    // 엑세스 토큰 로드
    private fun loadAccessToken(): String? {
        val sharedPreferences = context.getSharedPreferences("recipia", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }

    // 리프레시 토큰 저장
    fun saveRefreshToken(refreshToken: String) {
        val sharedPreferences = context.getSharedPreferences("recipia", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("refresh_token", refreshToken)
        editor.apply()
    }

    // 리프레시 토큰 로드
    private fun loadRefreshToken(): String? {
        val sharedPreferences = context.getSharedPreferences("recipia", Context.MODE_PRIVATE)
        return sharedPreferences.getString("refresh_token", null)
    }

    // 멤버id 저장
    fun saveMemberId(memberId: Long) {
        val sharedPreferences = context.getSharedPreferences("recipia", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("memberId", memberId)
        editor.apply()
    }

    // 요청 헤더에 엑세스 토큰 추가
    fun addAccessTokenToHeader(chain: Interceptor.Chain): Request {
        val request = chain.request()
        val accessToken = loadAccessToken()

        if (!accessToken.isNullOrBlank()) {
            return request.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        }

        return request
    }

    // 유효한 엑세스 토큰이 있는지 확인
    fun hasValidAccessToken(): Boolean {
        val accessToken = loadAccessToken()
        return !accessToken.isNullOrBlank()
    }
}
