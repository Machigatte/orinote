/**
 * @type {import('semantic-release').GlobalConfig}
 */
export default {
  branches: [
    "main",
    {
      "name": "dev",
      "prerelease": "beta"
    }
  ],

  plugins: [
    // 分析提交信息，决定下一个版本号 (major/minor/patch)
    "@semantic-release/commit-analyzer",

    // 根据提交信息生成变更日志内容
    "@semantic-release/release-notes-generator",

    // 核心插件：执行 Gradle 命令来管理版本号和部署
    ["@semantic-release/exec", {
      "prepareCmd": "bash release-build.sh ${nextRelease.version} ${nextRelease.channel ? '--prerelease' : ''}",
      "successCmd": "echo 'Semantic Release Successful!'"
    }],

    // 将更新后的版本号提交到 Git 并创建标签 (Tag)
    ["@semantic-release/git", {
      "assets": [
        // 必须包含你的 gradle.properties 和 build.gradle，因为版本号被 prepareCmd 更改了
        "gradle.properties",
        "build.gradle", 
        // 变更日志文件
        "CHANGELOG.md", 
      ],
      "message": "chore(release): release ${nextRelease.version} [skip ci]\n\n${nextRelease.notes}"
    }],
    
    // 如果使用 GitHub，此插件会自动创建 Release Notes，并可以上传构建产物
    ["@semantic-release/github", {
      prerelease: '${nextRelease.channel}',
      "assets": [
        { "path": "build/libs/!(*-plain).jar" }
      ]
    }], 
    
    // 生成 CHANGELOG.md 文件
    ["@semantic-release/changelog", {
      "changelogFile": "CHANGELOG.md",
    }],
  ],
};
