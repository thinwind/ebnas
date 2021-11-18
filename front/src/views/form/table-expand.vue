<style scoped>
    .expand-row{
        margin-bottom: 16px;
    }
</style>
<template>
    <div>
        <Row class="expand-row" v-for="(item, i) in row.nodes">
            <Col span="12">
                <span class="expand-key">节点IP: </span>
                <span class="expand-value">{{ item.ip }} : {{ item.port}} </span>
            </Col>
            <Col span="12">
                <span class="expand-key">节点状态: </span>
                <span class="expand-value">{{ item.status }}</span>
            </Col>
        </Row>
        
    </div>
</template>
<script>
    export default {
        props: {
            row: Object
        }
    };
</script>