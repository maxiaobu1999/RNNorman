import React, {Component} from 'react';

import {
    FlatList,
    StyleSheet,
    Text,
    TouchableHighlight,
    View,
    Image,
    Platform, // 判断当前运行的系统
    Navigator,
} from 'react-native';
import {conversionTime} from '../util/Utils.js';

/** 三张图片 */
export default class ThreePicCell extends Component {
    render() {
        let {movie} = this.props;
        return (
            <TouchableHighlight onPress={this.props.onPress}>
                <View style={styles.container}>
                    <Text numberOfLines={2} style={styles.title}>{movie.title}</Text>
                    {/*中间三张图*/}
                    <View style={styles.middleContainer}>
                        <Image
                            source={{uri: 'https://maqinglong-1253423006.cos.ap-beijing-1.myqcloud.com/' + movie.imageUrlList[0]}}
                            style={styles.thumbnail1}
                        />
                        <Image
                            source={{uri: 'https://maqinglong-1253423006.cos.ap-beijing-1.myqcloud.com/' + movie.imageUrlList[1]}}
                            style={styles.thumbnail2}
                        />
                        <Image
                            source={{uri: 'https://maqinglong-1253423006.cos.ap-beijing-1.myqcloud.com/' + movie.imageUrlList[2]}}
                            style={styles.thumbnail3}
                        />
                    </View>
                    <View style={styles.bottomContainer}>
                        {/*作者*/}
                        <Text style={styles.textBottom}>{movie.author}  </Text>
                        {/*评论数量*/}
                        <Text style={styles.textBottom}>{movie.commentCount}评论 </Text>
                        {/*评论时间*/}
                        <Text style={styles.textBottom}>{conversionTime(movie.behotTime * 1000)}</Text>
                    </View>
                </View>
            </TouchableHighlight>
        );
    }
}
const styles = StyleSheet.create({
    container: {
        // 决定其子元素沿着主轴的排列方式
        // justifyContent: 'center',
        // 决定其子元素沿着次轴的排列方式
        // alignItems: 'center',
        backgroundColor: '#ffffff',
        padding: 10,
        borderBottomWidth: 1,
        borderColor: '#e0e0e0',
    },
    thumbnail1: {
        flex: 1,
        width: 130,
        height: 80,
        backgroundColor: '#f0f0f0',
    },
    thumbnail2: {
        flex: 1,
        width: 130,
        height: 80,
        marginLeft: 5,
        backgroundColor: '#f0f0f0',
    },
    thumbnail3: {
        flex: 1,
        width: 130,
        height: 80,
        marginLeft: 5,
        backgroundColor: '#f0f0f0',
    },
    middleContainer: {
        // paddingRight: 10,
        // 横向布局
        flexDirection: 'row',
        paddingTop: 5,
        paddingBottom: 5,
    },
    bottomContainer: {
        // 横向布局
        flexDirection: 'row',
        // 决定其子元素沿着主轴的排列方式
        justifyContent: 'flex-start',
    },
    title: {
        fontSize: 14,
        fontWeight: 'bold',
        color: '#333333',
        textAlign: 'left',
    },
    textBottom: {
        fontSize: 10,
        textAlign: 'left',
        color: '#999999',
        marginTop: 10,
    },
    horizontalView: {
        flexDirection: 'row',
        marginTop: 10,
    },
    titleTag: {
        color: '#666666',
    },
    score: {
        color: '#ff8800',
        fontWeight: 'bold',
    },
    name: {
        color: '#333333',
        flex: 1,
    },
});
