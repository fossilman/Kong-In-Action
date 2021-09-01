const CompressionWebpackPlugin = require('compression-webpack-plugin')

// devServer配置查看 https://cli.vuejs.org/config/#devserver
const devServer = {
    host: '0.0.0.0',
    proxy: {
        // 权限
        '^/inneruser': {
            target: 'http://172.19.21.245:8080',
            // target: 'http://192.168.5.20:8080',
            changeOrigin: true,
            // pxathRewrite: {
            //     '^/public': 'public'
            // }
        },
        '^/publish': {
            // target: 'http://172.0.10.80:8080',
            target: 'http://172.19.22.2:8080',
            changeOrigin: true,
            // pxathRewrite: {
            //     '^/public': 'public'
            // }
        },
        '^/devopscenter': {
            // target: 'http://172.0.10.80:8080',
            target: 'http://devopscenter.dev.whalepms.com:8080',
            changeOrigin: true,
            // pxathRewrite: {
            //     '^/public': 'public'
            // }
        },
        '^/confplus': {
            //target: 'http://192.168.81.172:8080',
            target: 'http://confplus.dev.whalepms.com:8080',
            changeOrigin: true,
            // pathRewrite: {
            //     '^/public': 'public'
            // }
        },
        '^/schedulerplus': {
            //target: 'http://172.19.21.242:8080',
            target: 'http://schedulerplus.dev.whalepms.com:8080',
            changeOrigin: true,
            // pathRewrite: {
            //     '^/public': 'public'
            // }
        },
        '^/mockplus': {
            target: 'http://172.19.22.12:8080',
            // target: 'http://192.168.5.195:18080',
            changeOrigin: true,
            // pathRewrite: {
            //     '^/public': 'public'
            // }
        },
        '^/rabbitmqplus': {
            // target: 'http://172.19.22.69:8080',
            target: 'http://rabbitmqplus.dev.whalepms.com:8080',
            changeOrigin: true,
            // pathRewrite: {
            //     '^/public': 'public'
            // }
        },
        '^/routeplus': {
            target: 'http://172.19.22.89:8080',
            changeOrigin: true,
            // pathRewrite: {
            //     '^/public': 'public'
            // }
        },
    },
}

let workers = require('os').cpus().length

if (typeof workers !== 'number') {
    workers = 1
}

module.exports = {
    devServer: devServer,
    configureWebpack: (config) => {
        if (process.env.NODE_ENV === 'production') {
            //GZIP压缩
            config.plugins.push(
                new CompressionWebpackPlugin({
                    test: /\.(js|css|svg)(\?.*)?$/i, //需要压缩的文件正则
                    threshold: 4096, //文件大小大于这个值时启用压缩
                    deleteOriginalAssets: false, //压缩后是否删除原文件
                })
            )
        }
    },
    chainWebpack: (config) => {
        // see TerserPlugin compress options document:
        // https://github.com/terser/terser#compress-options
        if (process.env.NODE_ENV === 'production') {
            config.optimization.minimizer('terser').tap((args) => {
                args[0].terserOptions.parallel = workers > 1 // 开启多线程
                args[0].terserOptions.cache = true
                if (process.env.VUE_APP_MODE === 'prod') {
                    args[0].terserOptions.compress.drop_console = true
                    args[0].terserOptions.compress.pure_funcs = ['console.log']
                    args[0].terserOptions.compress.drop_debugger = true
                }
                return args
            })
        }

        //忽略的打包文件
        config.externals({
            vue: 'Vue',
            'vue-router': 'VueRouter',
            vuex: 'Vuex',
            axios: 'axios',
            'element-ui': 'ELEMENT',
        })

        // 去除默认配置中import后缀自动检测中多余的文件后缀
        config.resolve.extensions
            .clear()
            .add('.vue')
            .add('.ts')
            .add('.js')
        // 为vue开启thread-loader
        config.module
            .rule('vue')
            .use('thread-loader')
            .loader('thread-loader')
            .options({
                workers: workers,
            })
            .after('cache-loader')
    },
}
