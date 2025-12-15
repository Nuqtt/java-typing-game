# 🦖 English Typing Runner Game (英文打字跑酷遊戲)

![Java](https://img.shields.io/badge/Language-Java_11%2B-orange?style=for-the-badge&logo=java)
![Swing](https://img.shields.io/badge/GUI-Swing-blue?style=for-the-badge&logo=java)
![MongoDB](https://img.shields.io/badge/Database-MongoDB-green?style=for-the-badge&logo=mongodb)
![License](https://img.shields.io/badge/License-MIT-lightgrey?style=for-the-badge)

> 一款節奏明快、寓教於樂的無限跑酷遊戲。結合打字速度與生存挑戰，靈感來自 Chrome 離線恐龍遊戲，但奔跑的動力源自你的英文單字量！

<img width="1197" height="490" alt="image" src="https://github.com/user-attachments/assets/afc03538-cad3-4eca-ac98-6e5a4b0c2390" />


## ✨ 專案特色 (Features)

- **⌨️ 打字核心玩法**：輸入障礙物上方的單字，完成輸入即可觸發自動跳躍。
- **🏃 無限跑酷機制**：程序化生成的障礙物，隨時間增加難度（速度與生成頻率提升）。
- **🎨 精緻視覺效果**：
  - **精靈動畫 (Sprite Animation)**：流暢的恐龍奔跑動作（基於序列幀動畫）。
  - **視差捲動 (Parallax Scrolling)**：動態雲朵背景，營造場景深度感。
- **🏆 排行榜系統**：
  - **雙重儲存架構**：支援 **記憶體 (In-Memory)**（暫存）與 **MongoDB**（持久化）兩種模式。
  - **Top 5 排名**：遊戲結束後即時顯示最佳生存時間排行。
- **📚 智慧單字庫**：
  - 從外部 JSON 檔案讀取超過 1000 個常用單字。
  - **穩健備援機制 (Robust Fallback)**：若外部檔案遺失，自動切換至內建備用單字表，防止程式崩潰。
- **⚙️ 高度可配置**：所有遊戲參數（速度、重力、生成率）皆可在 `GameConfig.java` 中調整。

## 🛠 專案結構 (Project Structure)

```text
typing-game/
├── lib/                     # MongoDB 驅動程式與依賴庫
│   ├── mongodb-driver-sync-x.x.x.jar
│   ├── mongodb-driver-core-x.x.x.jar
│   ├── bson-x.x.x.jar
│   └── slf4j-api-x.x.x.jar
├── src/
│   └── typinggame/
│       ├── assets/          # 遊戲資源：圖片 (png) & 字典 (json)
│       ├── GameEngine.java  # 核心邏輯與狀態管理 (Model)
│       ├── GamePanel.java   # UI 渲染與輸入監聽 (View)
│       ├── ...              # 其他遊戲類別
└── bin/                     # 編譯輸出目錄
