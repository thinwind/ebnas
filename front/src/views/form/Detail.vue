
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
      axios.get("/api/dashboard/detail")
        .then((response) => {
          response.data.forEach((item,i)=>{
            
            item.data.forEach((d, j)=>{
              this.data1.push({});
              this.data1[j].count=d.nodes.length;
              this.data1[j].name=d.name;
              this.data1[j].nodes=d.nodes;
              
            });
            
          })
          // this.data1 = response.data;

        });
    }
  }
}
</script>

<style>

</style>