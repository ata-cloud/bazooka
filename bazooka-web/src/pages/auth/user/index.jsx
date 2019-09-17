import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Form, Input, Row, Col, Select, Button, Table, Divider, Popconfirm, message, Popover } from 'antd';
import { connect } from 'dva';
import moment from 'moment';
import { auth } from '@/services/auth';
import { query } from '@/services/user';
// import { accountStatus } from '@/common/constant';
import styles from '../../index.less';
import EditModal from '@/pages/auth/user/EditModal';
const FormItem = Form.Item;
const { Option } = Select;
// let obj = {};
class User extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchObj: {
      },
      showEdit: false,
      userId: undefined,
      submitLoading: false
    };
  }
  // --------------------生命周期 --------------------------------------------------
  componentDidMount() {
    const { list } = this.props;
    if (Object.getOwnPropertyNames(list).length === 0) {
      this.onFetchList();
    }
    // this.setState({
    //   searchObj: obj
    // }, () => {
    //   if (Object.getOwnPropertyNames(list).length === 0) {
    //     this.onFetchList();
    //   }
    // })
    // this.onFetchItems();
  }
  componentDidUpdate(prevProps, prevState) {
    const { searchObj } = this.state;
    if (searchObj !== prevState.searchObj) {
      this.onFetchList()
    }
  }
  componentWillUnmount() {
    // const { searchObj } = this.state;
    // obj = searchObj;
  }
  // --------------------方法 --------------------------------------------------
  //列表
  onFetchList = () => {
    const { searchObj } = this.state;
    const { dispatch } = this.props;
    dispatch({
      type: 'auth/userList',
      payload: searchObj
    })
  }
  onFetchItems = () => {
    const { dispatch, roleList } = this.props;
    if (Object.getOwnPropertyNames(roleList).length === 0 || roleList.pageSize != 100) {
      dispatch({
        type: 'auth/roleList',
        payload: {
          pageSize: 100
        }
      })
    }
  }
  onFetchUserAll = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'auth/userAll',
      payload: {}
    })
  }
  //分页,筛选,排序
  onTableChange = (pagination, filters, sorter) => {
    const { current, pageSize } = pagination;
    const { order, field } = sorter;
    const { searchObj } = this.state;
    const { list } = this.props;
    let sort = order ? {
      sortValue: order === 'ascend' ? 'asc' : order === 'descend' ? 'desc' : '',
      sortName: field ? field : ''
    } : {};
    let params = {
      ...searchObj,
      ...sort,
      pageNo: pageSize === list.pageSize ? parseInt(current) : 1,
      pageSize: parseInt(pageSize),
    };
    this.setState({
      searchObj: params
    });
  };
  //点击编辑or添加，显示弹窗
  onEdit = (record = {}) => {
    this.setState({
      userId: record.userId,
      showEdit: true
    })
  };
  //删除
  onDelete = async (userId) => {
    const { searchObj } = this.state;
    let res = await auth.userDelete({ userId });
    if (res && res.code == '1') {
      message.success('删除成功');
      this.setState({
        searchObj: {
          ...searchObj,
          pageNo: 1
        }
      })
      //更新用户全量
      this.onFetchUserAll()
    }
  }
  onCancel = () => {
    this.setState({
      showEdit: false
    })
  }
  //编辑Or添加
  onOk = async (values) => {
    const { userId, searchObj } = this.state;
    this.setState({
      submitLoading: true
    })
    if (userId) {
      //修改
      let res = await auth.userEdit({ ...values, userId });
      if (res && res.code === '1') {
        this.onCancel();
        message.success('编辑成功');
        this.onFetchList();
        //更新用户全量
        this.onFetchUserAll()
      }
      this.setState({
        submitLoading: false
      })
    } else {
      //添加
      let res = await auth.userAdd({ ...values });
      if (res && res.code === '1') {
        this.onCancel();
        message.success('添加成功');
        this.setState({
          searchObj: {
            ...searchObj,
            pageNo: 1
          }
        })
        //更新用户全量
        this.onFetchUserAll()
      }
      this.setState({
        submitLoading: false
      })
    }
  }
  onSearch = (value) => {
    const { searchObj } = this.state;
    this.setState({
      searchObj: {
        ...searchObj,
        condition: value
      }
    })
  }
  // --------------------渲染 --------------------------------------------------

  renderOpera() {
    return (
      <div className={styles.flexCenter}>
        <Input.Search onSearch={this.onSearch} placeholder="搜索用户" />
        <Button type="primary" onClick={() => this.onEdit()} className={styles.marginL}>
          + 新增用户
        </Button>
      </div>
    );
  }
  renderTable() {
    const { loading, list } = this.props;
    const columns = [
      {
        title: '用户名',
        dataIndex: 'username',
      },
      {
        title: '全名',
        dataIndex: 'realName',
      },
      {
        title: '邮箱',
        dataIndex: 'email'
      },
      {
        title: '角色',
        dataIndex: 'userRoles',
        className: styles.textOver,
        width: 200,
        render: (text, record) => (
          <Fragment>
            {
              text.map((item, i) => (
                <span key={i}>{item.roleName}{i < text.length - 1 ? '，' : ""}</span>
              ))
            }
          </Fragment>
        )
      },
      {
        title: '修改人',
        dataIndex: 'updateAuthor',
      },
      {
        title: '修改时间',
        dataIndex: 'updateTime',
        render: (text, record) => (
          <span>{text ? moment(text).format('YYYY-MM-DD HH:mm:ss') : ''}</span>
        )
      },
      {
        title: '操作',
        dataIndex: 'opera',
        render: (text, record) => (
          <div>
            <span>
              <a
                onClick={() => {
                  this.onEdit(record);
                }}
              >
                修改
              </a>
              <Divider type="vertical" />
              <Popconfirm
                title="确定删除吗?"
                onConfirm={() => this.onDelete(record.userId)}
                okText="确定"
                cancelText="取消"
              >
                <a>删除</a>
              </Popconfirm>
            </span>
          </div>
        ),
      },
    ];
    return (
      <Fragment>
        <div>
          <Table
            columns={columns}
            dataSource={list.rows || []}
            onChange={this.onTableChange}
            loading={loading}
            pagination={{
              pageSizeOptions: ['10', '20', '30', '50'],
              total: list.totalCount || 0,
              showTotal: (total, range) =>
                `共${list.totalCount || 0}条，当前${list.pageNum ? list.pageNum : 1}/${
                list.totalPage ? list.totalPage : 1
                }页`,
              showSizeChanger: true,
              current: list.pageNum ? list.pageNum : 1,
              pageSize: list.pageSize ? list.pageSize : 10,
            }}
            rowKey={record => {
              return record.userId;
            }}
          />
        </div>
      </Fragment>
    );
  }
  render() {
    const { showEdit, userId, submitLoading } = this.state;
    return (
      <Card title="用户列表" extra={
        this.renderOpera()
      } bordered={false}>
        <div className={styles.marginT} >
          {this.renderTable()}
          {showEdit && <EditModal visible={showEdit} userId={userId} onCancel={this.onCancel} onOk={this.onOk} submitLoading={submitLoading}/>}
        </div >
      </Card>
    );
  }
}
export default Form.create()(connect(({ auth, loading }) => ({
  list: auth.userList,
  roleList: auth.roleList,
  loading: loading.effects["auth/userList"]
}))(User));
