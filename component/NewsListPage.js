import React, {Component} from 'react';
import {
    FlatList,
    StyleSheet,
    Text,
    View,
    NativeModules,
    Platform, // 判断当前运行的系统
    Navigator,
} from 'react-native';
import TextCell from './cell/TextCell.js';
import CenterPicCell from './cell/CenterPicCell.js';
import RightPicCell from './cell/RightPicCell.js';
import ThreePicCell from './cell/ThreePicCell.js';
import RefreshListView from './Refresh/RefreshListView.js';
import RefreshState from './Refresh/RefreshState';

var nativeModule = NativeModules.OpenNativeModule;

// Component是一切界面组件的根类,实现界面组件必须要像继承此类
class NewsListPage extends Component {
    constructor() {
        super();
        this.state = {
            movieList: [], // 电影列表的数据源
            loaded: false, // 用来控制loading视图的显示，当数据加载完成，loading视图不再显示
        };
    }

    // 在render()之后立即执行；
    componentDidMount(): void {
        this.listView.beginHeaderRefresh();
    }

    render() {
        // for (let i = 0; i < this.state.movieList.length - 1; i++) {
        //     console.debug('+++++++++++++++1');
        //     console.debug(this.state.movieList[i].id);
        // }
        return (
            <RefreshListView
                ref={(ref) => {
                    this.listView = ref;
                }}
                data={this.state.movieList}
                renderItem={this._renderItem}
                // 用于生成一个唯一的key作为列表中每一项的身份标识
                keyExtractor={item => item.id}
                ListEmptyComponent={this._renderEmptyView}
                onHeaderRefresh={() => {
                    this._initData();
                }}
                onFooterRefresh={() => {
                    this.loadMoreData();
                }}
            />
        );
    }

    /** 创建item的视图*/
    _renderItem = item => {
        // 纯文字布局
        if (item.item.layoutType === 0) {
            return (
                <TextCell
                    movie={item.item}
                    onPress={() => {
                        let json = this._entityToJson(item.item);
                        nativeModule.openNativeVC(json);
                    }}
                />
            );
        }
        // 居中大图布局
        if (item.item.layoutType === 1) {
            return (
                <CenterPicCell
                    movie={item.item}
                    onPress={() => {
                        let json = this._entityToJson(item.item);
                        nativeModule.openNativeVC(json);
                    }}
                />
            );
        }
        // 右侧小图布局
        if (item.item.layoutType === 2) {
            return (
                <RightPicCell
                    movie={item.item}
                    onPress={() => {
                        let json = this._entityToJson(item.item);
                        nativeModule.openNativeVC(json);
                    }}
                />
            );
        }
        // 三张图片布局
        return (
            <ThreePicCell
                movie={item.item}
                onPress={() => {
                    console.debug('点击了电影----' + item.item.title); let json = this._entityToJson(item.item);
                    nativeModule.openNativeVC(json);
                }}
            />
        );
    };
    // 渲染一个空白页，当列表无数据的时候显示。这里简单写成一个View控件
    _renderEmptyView = (item) => {
        return <View/>;
    };

    /** 获取数据 */
    _initData() {
        let that = this;
        fetch('http://111.229.116.167:8088/news/queryByNum?num=10&channelId=0')
            .then(response => response.json())
            .then(responseJson => {
                for (let idx in responseJson.data) {
                    let newsItem = responseJson.data[idx];
                    newsItem.id = idx;
                }
                that.setState({
                    movieList: responseJson.data,
                    loaded: true,
                });
                that.listView.endRefreshing(RefreshState.CanLoadMore);
                return responseJson.data;
            })
            .catch(error => {
                console.error(error);
            })
            .done();
    }

    loadMoreData() {
        let that = this;
        fetch('http://111.229.116.167:8088/news/queryByNum?num=10')
            .then(response => response.json())
            .then(responseJson => {
                // 数组追加
                let newsList = this.state.movieList.concat(responseJson.data);
                // 设置cell索引
                for (let idx in newsList) {
                    let newsItem = newsList[idx];
                    newsItem.id = idx;
                }
                that.setState({
                    movieList: newsList,
                    loaded: true,
                });
                that.listView.endRefreshing(RefreshState.CanLoadMore);
                return responseJson.data;
            })
            .catch(error => {
                console.error(error);
            })
            .done();
    }

    _entityToJson(obj) {
        // 对象转换为json
        let string = JSON.stringify(obj);


        return string;
    }

}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        paddingTop: 22,
    },
    item: {
        padding: 10,
        fontSize: 18,
        height: 44,
    },
});

// 输出组件类
export default NewsListPage;
// module.exports = NewsListApp;
