# SaveDemoApp

## 坂東さんのクラス図をもとに共通データクラスを実装
・saveボタン
　暗号化チェックがTureの場合、暗号化処理の後SharedPreferencesに保存される。
 
 ※暗号化チェック・暗号化処理は、動くことを確認したが、
 　保存処理のSharedPreferences取得がどうしてもうまくいかない状態です。
   mainActivity上で呼ぶと、正常に動く
