module.exports = {
    context: __dirname + '/src/main/resources/static',
    entry: {
        JiraServerAgileExtended: './js/JiraServerAgileExtended.js',
        HierarchyFieldConfiguration: './js/admin/HierarchyFieldConfiguration.js',
        BacklogFieldConfiguration: './js/admin/BacklogFieldConfiguration.js'
    },
    output: {
        path: __dirname + '/src/main/resources/static/dist',
        filename: '[name].js'
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /(node_modules)/,
                loader: 'babel-loader',
                options: {
                    presets: ['@babel/preset-env']
                }
            }
        ]
    }
};