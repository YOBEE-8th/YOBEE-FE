# 레시피 목록

<img src="https://user-images.githubusercontent.com/77010707/230291315-a2f3ae10-51c7-493f-806e-a1d4d768862f.gif"  width="200" height="400"/>

### **Pagination**

> 서버로 부터 20개의 데이터를 받아 RecyclerView Adapter에 추가.

### **레시피 좋아요**

> 레시피 좋아요 요청을 서버로 전송한 뒤 응답을 받았을 때 NotifyDataSetChanged()가 호출되어 화면이 깜박거리는 현상이 있었다. 

```
이를 해결하기 위해 DiffUtil을 구현했고 NotifyDataSetChanged()를 호출하는 대신 submitList()를 호출했지만 뷰가 갱신되지 않았다. 

자료를 찾아보니 GridLayoutManager는 아이템 재배치를 리사이클러뷰가 스크롤되었을때 실시하는 것이 원인이었다.

따라서 몇번째 아이템이 갱신되었는지 추가로 전달해주었고 notifyItemChanged()를 사용하여 문제를 해결했다.
```