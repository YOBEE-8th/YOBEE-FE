# 검색

JsonArray 형태로 검색 기록을 SharedPreference에 기록, 삭제를 구현했다.



```kotlin
/** 현재 저장된 검색어 가져오기 */
fun getKeywordList(): List<String> {
    return decodeJSONArray(prefs.getString(KEY, null))
}
```
---
<br>

JSONstring을 매개변수로 받아서 JSONArray형태의 객체로 변환하고 인덱스의 문자열을 하나씩 keywordList에 추가한다.

![image](https://user-images.githubusercontent.com/77010707/230294735-2b606a20-6bfc-4d1a-a2a1-61ae8f82b250.png)

```kotlin
private fun decodeJSONArray(json: String?): List<String> {
    val keywordList = mutableListOf<String>()

    if (json != null) {
        val jsonArray = JSONArray(json)
        for (idx in 0 until jsonArray.length()) {
            keywordList.add(jsonArray.optString(idx))
        }
    }
    return keywordList
}
```
---
<br>

이미 저장돼있는 검색어를 다시 검색했을 때 인덱스 위치를 앞으로 당겨주기 위해 기존 값을 삭제하고 다시 put하는 방식으로 구현했다.

```kotlin
/** SharedPreference에 데이터 저장 */
fun addKeyword(value: String) {
    val baseList = getKeywordList().toMutableList()
    if (baseList.contains(value)) {
        baseList.remove(value)
    }
    val jsonArray = JSONArray()

    jsonArray.put(value)
    baseList.forEach { keyword ->
        jsonArray.put(keyword)
    }

    prefs.edit().putString(KEY, jsonArray.toString()).apply()
}
```