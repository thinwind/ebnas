<template>
  <div>详细页面
<span>{{ formData.title }}</span>
  </div>
  
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      formData: {}
    }
  },
  mounted() {
    this.getData();
    // this.interval = setInterval(() => {
    //   this.getData();
    // }, 3000);
  },
  methods: {
    getData() {
      axios
        .get("/openapi/clusters", { params: { delay: 0 } })
        .then((response) => {
          this.formData = response.data;
        });
    },
  },
  beforeDestroy() {
    clearInterval(this.interval);
  }
};
</script>

<style>
</style>