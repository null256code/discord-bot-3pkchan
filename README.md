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


| 変数名                           | 必須 | 説明                                                              |
| -------------------------------- | ---- | ----------------------------------------------------------------- |
| botconfig.discord.token          | ○   | Discord BotのTOKEN                                                |
| botconfig.discord.application_id | ○   | Discord BotのAPPLICATION ID                                       |
| botconfig.rakuten.application_id | ○   | [楽天ウェブサービス](https://webservice.rakuten.co.jp/)のアプリID |

### 実行

- `./gradlew bootRun` で動く

### (Windows向け) nativeCompile用環境構築＆実行の仕方
- [graalvm](https://community.chocolatey.org/packages/graalvm) をインストールする
- graalvmを `JAVA_HOME` に設定
- 2019以降のVisualStudioをDLして `C++に夜デスクトップ開発` をインストールする
  - コマンドラインでビルドするために必要
  - 2022でも多分いけるはず(2019でしか試していない)
- インストールされた `x64 Native Tools Command Prompt for VS 2019` を起動
- プロジェクトルートに移動し `gradlew nativeCompile` 実行

## deploy先

### 環境変数

- localで設定したものに加えて、SPRING_DATASOURCE_XXXを設定する
  - 詳しくはapplication.yml見て

# デプロイ

## herokuの場合

- `./gradlew docker` でイメージ作る
- `heroku container:login`
- `heroku container:push web`
- `heroku container:release web`
