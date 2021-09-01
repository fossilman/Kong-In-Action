<template>
    <div class="server-list">
        <div class="breadcrumb">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item :to="{ path: '/' }"
                    >首页</el-breadcrumb-item
                >
                <el-breadcrumb-item>服务器列表</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="add-btn">
            <el-button size="small" type="primary" @click="addBtn">
                <li class="el-icon-plus"></li>
                添加服务器
            </el-button>
        </div>
        <template>
            <el-table
                :data="list"
                v-loading="loading"
                stripe
                style="width: 100%"
            >
                <el-table-column prop="id" label="id编号"></el-table-column>
                <el-table-column prop="ip" label="IP地址"></el-table-column>
                <el-table-column prop="port" label="端口号"></el-table-column>
                <el-table-column prop="team" label="分组"></el-table-column>
                <el-table-column prop="name" label="名称"></el-table-column>
                <el-table-column prop="remark" label="备注"></el-table-column>
                <el-table-column
                    label="操作"
                    fixed="right"
                    width="100"
                    align="center"
                >
                    <template slot-scope="scope">
                        <el-button
                            size="small"
                            type="text"
                            @click="handleEdit(scope.row)"
                            >编辑</el-button
                        >
                        <el-button
                            size="small"
                            type="text"
                            @click="handleDelete(scope.row)"
                            >删除</el-button
                        >
                    </template>
                </el-table-column>
            </el-table>
        </template>
        <div class="pagination">
            <el-pagination
                :hide-on-single-page="true"
                @current-change="currentChange"
                background
                :page-size="size"
                layout="prev, pager, next"
                :total="total"
            ></el-pagination>
        </div>
    </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { serversList, serversDel } from '@/server/servers.ts';
@Component
export default class ServerList extends Vue {
    private current: number = 1;
    private size: number = 20;
    private total: number = 0;
    private list: any = [];
    private loading: boolean = false;

    private mounted(): void {
        this.getServersList();
    }

    private handleEdit(info: any): void {
        this.$router.push({
            path: '/server/edit',
            query: {
                id: info.id,
                ip: info.ip,
                name: info.name,
                remark: info.remark,
                team: info.team,
                port: info.port
            }
        });
    }

    private handleDelete(info: any): void {
        this.$confirm('是否要删除服务器吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
            .then(() => {
                serversDel(info.id)
                    .then((rs: any) => {
                        this.getServersList();
                    })
                    .catch((err: any) => {
                        this.$message({
                            message: err.errmsg,
                            type: 'warning'
                        });
                    });
            })
            .catch(() => {
                console.log(1);
            });
    }

    private getServersList(): void {
        this.loading = true;
        serversList(this.current, this.size)
            .then((rs: any) => {
                this.loading = false;
                this.list = rs.retval.serverList;
                this.total = rs.retval.total;
                console.log(this.total);
            })
            .catch((err: any) => {
                this.loading = true;
                this.$message({
                    message: err.errmsg,
                    type: 'warning'
                });
            });
    }
    private currentChange(current: number): void {
        this.current = current;
        this.getServersList();
    }

    private addBtn(): void {
        this.$router.push({
            path: '/server/save'
        });
    }
}
</script>

<style lang="scss">
.server-list {
    position: relative;
    .main-title {
        text-align: left;
        padding: 20px 10px;
    }
    .add-btn {
        padding: 15px 0;
        text-align: left;
        border-bottom: 1px solid #dcdcdc;
        margin-bottom: 15px;
    }
}
.breadcrumb {
    height: 30px;
    padding: 0 10px 10px 10px;
    border-bottom: 1px solid #dcdcdc;
}
.pagination {
    padding-top: 30px;
}
</style>
