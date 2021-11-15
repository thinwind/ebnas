
<template>
  <div>
    <span>详细列表</span>
    <Table stripe :columns="columns1" :data="data1"></Table>
  </div>
  
</template>

<script>
import expandRow from './table-expand.vue';
import axios from 'axios';

export default {
  components: { expandRow },
  data () {
    return {
        columns1: [
          {
            type: 'expand',
            width: 50,
            render: (h, params) => {
                return h(expandRow, {
                    props: {
                        row: params.row
                    }
                })
            }
          },
          {
              title: "集群名称",
              key: "name"
          },
          {
              title: "集群内节点个数",
              key: "count"
          }
        ],
        data1: []
    }
  },
  mounted() {
    this.getData();
  },
  methods: {
    getData() {
      axios.get("/openapi/clusters", { params: { delay: 0 } })
        .then((response) => {
          response.data.data.forEach((item,j)=>{
            
              this.data1.push({});
              this.data1[j].count=item.nodes.length;
              this.data1[j].name=item.name;
              this.data1[j].nodes=item.nodes;
              
            
          })
          // this.data1 = response.data;

        });
    }
  }
}
</script>

<style>

</style>