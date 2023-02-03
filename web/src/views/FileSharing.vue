<template>
  <div class="container">
    <header>
      <H2>WELCOME TO ANGEYA'S BOOK SHARING PLATFORM</H2>
    </header>
    <div class="main-container">
      <aside>
        <el-upload
            class="upload-demo"
            drag
            action="api/file-service/upload">
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">文件上传说明</div>
        </el-upload>
        <el-input
            type="textarea"
            autosize
            placeholder="请输入内容"
            v-model="tempText">
        </el-input>
        <el-button @click="saveTempText">Save</el-button>
        <el-button @click="saveTempText">Clean</el-button>
      </aside>
      <main>
        <el-table
            :data="fileList"
            height="500"
            border
            style="width: 100%">
          <el-table-column prop="name" label="File Name" width="200" align="center"></el-table-column>
          <el-table-column prop="size" label="File Size" width="200" align="center">
            <template scope="scope">
              <span>{{scope.row.size / 1000}}kb</span>
            </template>
          </el-table-column>
          <el-table-column prop="dateTime" label="Create Time" width="300"  align="center"></el-table-column>
          <el-table-column prop="" label="Operation" width="300">
            <el-button @click="saveTempText">Download</el-button>
            <el-button @click="saveTempText">Delete</el-button>
          </el-table-column>
        </el-table>
      </main>
    </div>
    <footer>
      <p>There is nothing in the footer!</p>
    </footer>
  </div>
</template>

<script>
export default {
  name: "FileSharing",
  data() {
    return {
      tempText: '',
      fileList: []
    }
  },
  methods: {
    switchFileSizeUnit(size) {

    },
    getTempText() {
      this.axios.get('/file-service/temp-text').then(res => {
        this.tempText = res.data
      })
    },
    saveTempText(clean = false) {
      if (clean) {
        this.tempText = ''
      }
      this.axios.put('/file-service/temp-text', {
        content: this.tempText
      }).then(res => {
        // do nothing
      })
    },
    getFileList() {
      this.axios.get('/file-service/file-list').then(res => {
        this.fileList = res.data
      })
    }
  },
  mounted() {
    this.getTempText()
    this.getFileList()
  }
}
</script>

<style scoped lang="stylus">
.container
  background-color aliceblue
  height 100vh
  margin 0 10px
  header
    height 12vh
    line-height 10vh
    border-bottom 1px solid black
  .main-container
    display flex
    height calc(100% - 14vh - 2px)
    aside
      border-right 1px solid black
      width 364px
  footer
    height 4vh
    border-top 1px solid black
</style>