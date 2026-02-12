[🇺🇸 English](./README.md)

# Messay Coding Challenge

Messayのコーディング試験へようこそ！ このリポジトリは、Kotlin Multiplatform (KMP) で構築されたスケルトンプロジェクトです。
あなたのミッションは、このプロジェクトをベースに、弊社独自の視線トラッキングライブラリ 「Messay Development Kit (MDK)」 を使用したデモ画面を実装することです。

## 🎯 課題内容

「視線で操作する新しいユーザー体験のデモ画面」 を実装してください。
`libs.versions.toml`には既に Messay Development Kit が定義されています。このライブラリのAPIを使用して、以下の要件を満たす画面を作成してください。
弊社の方から、Messay Development Kitを有効にするための、ユーザー名とトークンをお送りするので、`local.properties`に以下のように追記してください。
local.properties
```
maven.messay.username=your-username
maven.messay.password=your-token
```

### 必須要件（Requirements）

1. Messay SDKの組み込み:
   `composeApp` 内でSDKを適切に初期化し、視線データ（座標や検知イベントなど）を取得できる状態にすること。

2. インタラクティブなUI:
   `MdkTarget.EyeCloseHold`と`MdkTarget.FaceMovement`を使って、目を一定時間とじてから開くことで発火する操作と、顔の動きを使ったポインターの操作を、１つの画面（`App.kt`）に実装してください。

3. プラットフォーム:
   Android: 必須。エミュレータまたは実機で動作すること。
   iOS: オプション（環境がある場合）。KMPの特性を活かし、ロジックが共有されていることが望ましいです。

### 使用技術 (Tech Stack)

- Language: Kotlin

- UI Framework: Compose Multiplatform

- Architecture: 指定はありませんが、可読性とメンテナンス性を意識した構成（MVVM, MVIなど）を推奨します。

## 🛠 Messay Development Kit (MDK)について
インカメラから取得した画像から、目の開閉や顔の角度、虹彩の位置などを検出し、ユーザーインタラクションに適したデータ形式に変換する、弊社独自のライブラリです。

具体的な使い方は、こちらの[ドキュメント](https://honey-sassafras-fe0.notion.site/MDK-Implementation-Guide-30e6ae08ad1f47799402bf6c83e172f2)にまとめてあります。

## 📂 プロジェクト構成
このプロジェクトは標準的なKotlin Multiplatform構成に従っています。

`/composeApp`: Compose Multiplatformの共有コードおよび各プラットフォームの実装が含まれます。

`commonMain`: Android/iOS共通のロジックとUI（Compose）を記述します。基本的にはここにコードを書いてください。

`androidMain`: Android固有の実装（Cameraパーミッションのハンドリングなどが必要な場合）。

`iosMain`: iOS固有の実装。

`/iosApp`: iOSアプリケーションのエントリーポイント（Xcodeプロジェクト）。

## 🚀 ビルドと実行
Android アプリの実行
Android StudioのRun構成を使用するか、ターミナルから以下を実行してください。

macOS/Linux:
```
./gradlew :composeApp:assembleDebug
```

Windows:
```
.\gradlew.bat :composeApp:assembleDebug
```

iOS アプリの実行 (Optional)
Xcodeで `/iosApp`ディレクトリを開いて実行するか、Android Studio (KMPプラグイン導入済み) から実行してください。

## ✅ レビューポイント
提出されたコードは以下の観点でレビューします。

- 機能性:要件通りに視線トラッキングが動作しているか。

- 設計力: KMPの特性（共通化）を理解し、適切なアーキテクチャで実装されているか。

- UI/UX: 視線という特殊な入力に対し、ユーザーにフィードバック（視覚効果など）を適切に返せているか。

- コード品質: 命名規則、可読性、エラーハンドリング。

## 📦 提出方法
1. このリポジトリをフォーク、またはZIPでダウンロードしてください。

2. 課題完了後、GitHub等のリポジトリのURL、またはZIPファイルを共有してください。

3. `SUBMISSION.md`（任意）を作成し、アピールポイントや実装上の工夫があれば記載してください。

Good luck! We are looking forward to your code! 🚀