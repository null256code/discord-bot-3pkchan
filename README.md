# discord-bot-3pkchan

- discord用botのサーバサイドプログラム

# 環境構築

## local

### ミドルウェア

- `docker-compose up -d` 実行
  - postgres, pgadminが立ち上がる
  - 認証情報はdocker-compose.yml見て

### 環境変数

- Springの `@Value`で以下を設定しているので、実行時の環境変数などにそれぞれ設定しておく


| 変数名                              | 必須 | 説明                                                   |
|----------------------------------| ---- |------------------------------------------------------|
| botconfig.discord.token          | ○   | Discord BotのTOKEN                                    |
| botconfig.discord.application_id | ○   | Discord BotのAPPLICATION ID                           |
| botconfig.discord.server_id      | ○   | Discord BotのGUILD ID(サーバーID)、slash commandの登録に使用     |
| botconfig.rakuten.application_id | ○   | [楽天ウェブサービス](https://webservice.rakuten.co.jp/)のアプリID |
| botconfig.zaim.oauth.consumer_key | ○   | [Zaim developers](https://dev.zaim.net/)のコンシューマ ID     |
| botconfig.zaim.oauth.consumer_secret | ○   | [Zaim developers](https://dev.zaim.net/)のコンシューマシークレット |


### 実行

- `./gradlew bootRun` で動く

### (Windows向け) nativeCompileを試したいとき
- [graalvm](https://community.chocolatey.org/packages/graalvm) をインストールする
- graalvmを `JAVA_HOME` に設定
- 2019以降のVisualStudioをDLして `C++に夜デスクトップ開発` をインストールする
  - コマンドラインでビルドするために必要
  - 2022でも多分いけるはず(2019でしか試していない)
- インストールされた `x64 Native Tools Command Prompt for VS 2019` を起動
- プロジェクトルートに移動し `gradlew nativeCompile` 実行

## deploy先

### 環境変数

- 基本的にはlocalで設定したものを設定する
  - 追加でSPRING_DATASOURCE_XXXを設定する
    - 詳しくはapplication.yml見て
  - `botconfig.discord.server_id` はデプロイ環境では不要(GuildCommandとしてはslash commandを登録しないので)

# デプロイ

## herokuの場合

- [普通のherokuのdeploy](https://devcenter.heroku.com/ja/articles/deploying-java)をすればOK
